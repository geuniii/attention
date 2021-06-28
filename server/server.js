var mysql = require('mysql');
var express = require('express');
var bodyParser = require('body-parser');
var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json())

app.listen(3000, function () {
    console.log('start...');
});

var connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '3322',
});
connection.connect(function (err) {
    if (!err) {
        console.log("Database is connected ... \n\n");
    } else {
        console.log("Error connecting database ... \n\n");
    }
});


var STUDYSTART;
var studyId;
var greenTime;
var fullTime;
var userId;
var attention;
var startTime;
var endTime;


/**
 * 날짜,시간 변환
 * @param {*} str : 현재 시간
 * @returns :변환된 날짜 객체
 */
function dateParse(str) {
    var year = str.substring(0, 4);
    var month = str.substring(5, 7);
    var day = str.substring(8, 10);
    var hour = str.substring(11, 13);
    var minute = str.substring(14, 16);
    var second = str.substring(17, 19);

    var a = new Date(year, month, day, hour, minute, second);
    return a
}


/**
 * 오늘 날짜 구하기
 * @returns : 오늘 날짜
 */
function getToday() {
    var d = new Date();
    var s =
        leadingZeros(d.getFullYear(), 4) + '-' +
        leadingZeros(d.getMonth() + 1, 2) + '-' +
        leadingZeros(d.getDate(), 2) + ' ' +

        leadingZeros(d.getHours(), 2) + ':' +
        leadingZeros(d.getMinutes(), 2) + ':' +
        leadingZeros(d.getSeconds(), 2);

    return s;
}

/**
 * 내일 날짜 구하기
 * @returns : 내일 날짜
 */
function getNextday() {
    var d = new Date();
    d.setDate(d.getDate() + 1);
    var s =
        leadingZeros(d.getFullYear(), 4) + '-' +
        leadingZeros(d.getMonth() + 1, 2) + '-' +
        leadingZeros(d.getDate(), 2) + ' ' +

        leadingZeros(d.getHours(), 2) + ':' +
        leadingZeros(d.getMinutes(), 2) + ':' +
        leadingZeros(d.getSeconds(), 2);

    return s;
}

/**
 * 숫자 앞에 0 붙이기
 * @param {*} n : 숫자
 * @param {*} digits : 기준 길이
 * @returns : 변환된 숫자 문자
 */
function leadingZeros(n, digits) {
    var zero = '';
    n = n.toString();

    if (n.length < digits) {
        for (i = 0; i < digits - n.length; i++)
            zero += '0';
    }
    return zero + n;
}


/**
 * 나의 스터디 별 공부시간 조회
 */
app.post('/GroupChart', (req, res) => {

    console.log("조회하려는 studyId :", req.body.studyId)


    var sqlChart = "select sum(greenTime) sumgreenTime, userId from studytime where studyId=" + mysql.escape(req.body.studyId) + "  group by userId  "

    connection.query(sqlChart, function (err, rows, fields) {

        var arr = new Array()
        if (!err) {

            console.log(sqlChart)
            console.log('성공 : ');
            console.log("쿼리결과 :" + rows)

            for (var i = 0; i < rows.length; i++) {
                arr.push({ greenTime: rows[i].sumgreenTime, userId: rows[i].userId })

            }
            console.log(arr)
            console.log(JSON.stringify(arr))
            res.status(200).send(JSON.stringify(arr))//한번만 보내야 함
        }
        else {
            console.log('Error while performing Query.');
            console.log(err)
        }

    });

})


/**
 * 실시간 스터디 중인 스터디원 조회
 */
app.post('/userIdsearch', (req, res) => {

    var sqlSearch = "SELECT DISTINCT userId from studytime WHERE studyId =" + mysql.escape(req.body.studyId) +
        "AND userId !=" + mysql.escape(req.body.userId) + "AND studyEnd IS NOT NULL "

    connection.query(sqlSearch, function (err, rows, fields) {

        if (!err) {
            console.log('빨간불 user : ');

            var arr = new Array()


            for (var i = 0; i < rows.length; i++) {
                arr.push({ userId: rows[i].userId })
            }

            console.log(JSON.stringify(arr))
            res.status(200).send(JSON.stringify(arr))
        }
        else {
            console.log('Error while performing Query.');
            console.log(err)

        }

    });

})


/**
 * 스터디별 집중 시간 조회
 */
app.post('/Greensearch', (req, res) => {

    console.log(dateParse(req.body.currentTime))

    var sqlSearch = "SELECT studyStart,studyEnd,userId from studytime WHERE studyId =" + mysql.escape(req.body.studyId)

    connection.query(sqlSearch, function (err, rows, fields) {

        var arr = new Array()
        if (!err) {
            for (var i = 0; i < rows.length; i++) {
                if (req.body.userId != rows[i].userId) {

                    if (dateParse(req.body.currentTime) > dateParse(rows[i].studyStart)) {

                        if (!rows[i].studyEnd) {


                            arr.push({ userId: rows[i].userId })

                        }

                    }
                }
            }
            res.status(200).send(JSON.stringify(arr))
        }
        else {
            console.log('Error while performing Query.');
            console.log(err)
        }
    });
})


/**
 * 스터디 명 검색
 */
app.post('/StudySearch', (req, res) => {

    var sqlSearch = "select studyName from study where studyName like " + mysql.escape('%' + req.body.studyName + '%') + "group by studyId"
    connection.query(sqlSearch, function (err, rows, fields) {

        var arr = new Array()
        if (!err) {

            for (var i = 0; i < rows.length; i++) {

                arr.push({ studyName: rows[i].studyName })

            }
            console.log("보낼데이터 :\n", arr)

            res.status(200).send(JSON.stringify(arr))
        }
        else {
            console.log('Error while performing Query.');
            console.log(err)
        }

    });
})

/**
 * 스터디 방 입장
 */
app.post('/test', (req, res) => {

    const newUser = {
        studyId: req.body.studyId,
        greenTime: req.body.greenTime,
        fullTime: req.body.fullTime,
        userId: req.body.userId,
        startTime: req.body.startTime,
        endTime: req.body.endTime

    }

    studyId = parseInt(newUser.studyId)
    greenTime = parseInt(newUser.greenTime)
    fullTime = parseInt(newUser.fullTime)
    userId = newUser.userId
    attention = (greenTime / fullTime).toFixed(2)
    console.log("attention" + attention)
    startTime = newUser.startTime
    endTime = newUser.endTime

    STUDYSTART = startTime

    if (newUser.studyId == null) {
        res.status(400).send()


    } else {
        res.status(200).send()
    }


    //집중 시간 저장
    var sql = "INSERT INTO studytime VALUES " + "(" + mysql.escape(studyId) + "," + mysql.escape(greenTime) +
        "," + mysql.escape(fullTime) + "," + mysql.escape(userId) + "," + mysql.escape(startTime) + "," + mysql.escape(endTime) + "," + mysql.escape(req.body.latitude) + "," + mysql.escape(req.body.longitude) + ")"

    connection.query(sql, function (err, rows, fields) {

        if (!err) {
            console.log('성공 : ', rows);
        }
        else
            console.log('Error while performing Query.업데이트오류');
        console.log(err)

    });
});


/**
 * 스터디 방 종료, 공부 시간 기록
 */
app.post('/testEnd', (req, res) => {

    const newUser = {
        studyId: req.body.studyId,
        greenTime: req.body.greenTime,
        fullTime: req.body.fullTime,
        userId: req.body.userId,
        startTime: req.body.startTime,
        endTime: req.body.endTime

    }

    studyId = parseInt(newUser.studyId)
    greenTime = parseInt(newUser.greenTime)
    fullTime = parseInt(newUser.fullTime)
    userId = newUser.userId
    attention = (greenTime / fullTime).toFixed(2)
    console.log("attention" + attention)
    startTime = newUser.startTime
    endTime = newUser.endTime

    if (newUser.studyId == null) {
        res.status(400).send()

    } else {
        res.status(200).send()
    }

    console.log("스터디id" + studyId)

    var sql2 = "UPDATE studytime SET greenTime=" + mysql.escape(greenTime) + ", fullTime =" + mysql.escape(fullTime) +
        ",studyEnd =" + mysql.escape(endTime) + "where studyStart ='" + STUDYSTART + "'"


    connection.query(sql2, function (err, rows, fields) {

        if (!err) {
            console.log('성공 : ', rows);
        }
        else
            console.log('Error while performing Query.업데이트오류');
        console.log(err)

    });


});


/**
 * 스터디 가입
 */
app.post('/resisterStudy', function (req, res) {

    var sql = "select studyId, startDate,studyName,category,endDate  from study where studyName =" + mysql.escape(req.body.studyName) + ""

    connection.query(sql, function (err, result) {


        if (err) {
            console.log(err);

        } else {
            var insrt = "INSERT INTO `test`.`study` (`studyId`, `startDate`, `studyName`, `userId`, `category`, `endDate`) VALUES (?, ?, ?, ?, ?,?)"
            var params = [result[0].studyId, result[0].startDate, result[0].studyName, req.body.userId, result[0].category, result[0].endDate];

            connection.query(insrt, params, function (err, result2) {

                if (err) {
                    if (err.errno == 1062) {
                        res.status(400).send()
                    }

                } else {
                    res.status(200).send()

                }


            });

        }
    });
});

/**
 * 회원가입
 */
app.post('/register', function (req, res) {
    console.log(req.body);
    var userid = req.body.userid;
    var password = req.body.password;
    var age = req.body.age;
    var gender = req.body.gender;
    var sql = 'INSERT INTO users (userid,password,age,gender) VALUES (?, ?, ?, ?)';
    var params = [userid, password, age, gender];

    connection.query(sql, params, function (err, result) {
        var resultCode = 404;
        var message = 'error';

        if (err) {
            console.log(err);
        } else {
            resultCode = 200;
            message = 'sign up success';
        }

        res.json({
            'code': resultCode,
            'message': message
        });
    });
});


/**
 * 로그인
 */
app.post('/login', function (req, res) {
    var userid = req.body.userid;
    var password = req.body.password;
    var sql = 'select * from users where userid = ?';

    connection.query(sql, userid, function (err, result) {
        var resultCode = 404;
        var message = 'error';

        if (err) {
            console.log(err);
        } else {
            if (result.length === 0) {
                resultCode = 204;
                message = 'Nobody';
            } else if (password !== result[0].password) {
                resultCode = 204;
                message = 'Password Wrong';
            } else {
                resultCode = 200;
                message = 'Login Success';
            }
        }

        res.json({
            'code': resultCode,
            'message': message
        });
    })
});


/**
 * 스터디 개설
 */
app.post('/newStudy', function (req, res) {

    console.log(req.body);

    var category = req.body.category;
    var startDate = req.body.startDate;
    var endDate = req.body.endDate;
    var studyName = req.body.studyName;
    var userId = req.body.userId;
    var studyId;


    console.log(req.body);

    var sql = 'SELECT count(studyName) "last" from study';

    connection.query(sql, function (err, result) {
        var resultCode = 404;
        var message = 'error';

        if (err) {
            console.log(err);

        } else {
            resultCode = 200;
            message = 'study set up success';
            console.log(result)
            studyId = parseInt(result[0].last) + 1;
            console.log(studyId)

            var sql2 = 'INSERT INTO study (category,startDate,endDate,studyName,userId,studyId) VALUES (?, ?, ?, ?, ?, ?)';

            var params = [category, startDate, endDate, studyName, userId, studyId];

            connection.query(sql2, params, function (err, result) {
                var resultCode = 404;
                var message = 'error';

                if (err) {
                    console.log(err);
                } else {
                    resultCode = 200;
                    message = 'study set up success';
                }

            });

        }

        res.json({
            'code': resultCode,
            'message': message
        });
    });

});




/**
 * 공부방 입장 전 스터디 선택
 */
app.post('/enterStudy', function (req, res) {

    var sql = "select study.category, study.startDate,study.endDate,count(distinct study.userId) as member from study left outer join  studytime on study.studyId=studytime.studyId where study.studyName =" + mysql.escape(req.body.studyName) + ""

    connection.query(sql, function (err, result) {


        if (err) {
            console.log(err);

        } else {

            const newUser = {
                category: result[0].category,
                startDate: result[0].startDate,
                endDate: result[0].endDate,
                member: String(result[0].member)
            }
            res.status(200).send(JSON.stringify(result[0]))
        }


    });

});



/**
 * 스터디 가입
 */
app.post('/resisterStudy', function (req, res) {

    var sql = "select studyId, startDate,studyName,category,endDate  from study where studyName =" + mysql.escape(req.body.studyName) + ""

    connection.query(sql, function (err, result) {


        if (err) {
            console.log(err);

        } else {
            var insrt = "INSERT INTO `test`.`study` (`studyId`, `startDate`, `studyName`, `userId`, `category`, `endDate`) VALUES (?, ?, ?, ?, ?,?)"
            var params = [result[0].studyId, result[0].startDate, result[0].studyName, req.body.userId, result[0].category, result[0].endDate];



            connection.query(insrt, params, function (err, result2) {


                if (err) {
                    console.log(err);
                } else {
                    res.status(200).send()



                }


            });



        }


    });

});




/**
 * 공부시간 대비 집중시간
 */
app.post('/greenByFull', function (req, res) {

    var today = getToday().substring(0, 10)//yyyy-mm-dd 까지
    var tomorow = getNextday().substring(0, 10)

    // 오늘 날짜와 다음날짜 00시~02시 대 인것들만 셀렉트
    var sql = "select greenTime, fullTime ,studyStart from studytime where userId=" + mysql.escape(req.body.userId) + " or studyStart like "
        + mysql.escape('%' + today + '%') + "and studyStart like" + mysql.escape('%' + tomorow + ' 00' + '%') + mysql.escape('%' + today + '%') + "and studyStart like"
        + mysql.escape('%' + tomorow + ' 01' + '%') + mysql.escape('%' + today + '%') + "and studyStart like" + mysql.escape('%' + tomorow + ' 02' + '%')

    connection.query(sql, function (err, result) {

        console.log(sql)

        var array = new Array();

        if (err) {
            console.log(err);

        } else {
            for (var i = 0; i < result.length; i++) {

                if (result[i].studyStart.substring(0, 10) == today && parseInt(result[i].studyStart.substring(11, 13)) > 2) {
                    //오늘날짜이고 03시~23시 대
                    array.push(
                        {
                            time: result[i].studyStart.substring(11, 16),
                            green: Math.round(result[i].greenTime / 60), full: Math.round(result[i].fullTime / 60)
                        })

                    console.log(result[i].studyStart.substring(11, 16),
                        Math.round(result[i].greenTime / 60), Math.round(result[i].fullTime / 60))

                }

                if (result[i].studyStart.substring(0, 10) == tomorow && parseInt(result[i].studyStart.substring(11, 13)) < 3) {
                    //다음날짜이고 00시~02시 대

                    array.push(
                        {
                            time: result[i].studyStart.substring(11, 16),
                            green: Math.round(result[i].greenTime / 60), full: Math.round(result[i].fullTime / 60)
                        })
                    console.log(result[i].studyStart.substring(11, 16),
                        Math.round(result[i].greenTime / 60), Math.round(result[i].fullTime / 60))

                }

            }
            res.status(200).send(JSON.stringify(array))

        }
    });

});


/**
 * 집중 표정 수치 저장
 */
app.post('/attentionMinMax', function (req, res) {

    console.log(req.body.min, req.body.max);
    var select = "select userId from attentionface"
    connection.query(select, function (error, result2) {

        if (!error) {
            console.log('/attentionMinMax 1');

            for (var i = 0; i < result2.length; i++) {
                console.log('/attentionMinMax 2');

                if (result2[i].userId == req.body.userId) {
                    first = 0
                    console.log("조회 결괴" + result2[i].userId)


                    var update = "UPDATE attentionFace SET min = " + mysql.escape(parseFloat(req.body.min)) +
                        ",  max =" + mysql.escape(parseFloat(req.body.max)) + " WHERE userId = "
                        + mysql.escape(req.body.userId)


                    console.log("업뎃쿼리 :" + update)

                    connection.query(update, function (error2, reslut3) {


                        if (error2) {
                            console.log(error2);

                        }
                    });

                } else {
                    first = 1

                }


            }
            // 최초 등록일 경우
            if (first == 1) {
                var sql = "INSERT INTO attentionface VALUES " + "(" + mysql.escape(req.body.userId) + "," + mysql.escape(parseFloat(req.body.min)) +
                    "," + mysql.escape(parseFloat(req.body.max)) + ")"

                connection.query(sql, function (err, result) {

                    if (err) {
                        console.log(err);

                    }

                });

            }
            res.status(200).send()
        }


    })


});


/**
 * 사용자의 집중 표정 수치 범위 조회
 */
app.post('/attentionChk', function (req, res) {

    var sql = "select  min,max from attentionFace where userId =" + mysql.escape(req.body.userId)

    connection.query(sql, function (err, result) {
        if (err) {
            console.log(err);

        } else {
            res.status(200).send(JSON.stringify(result[0].min + "," + result[0].max))

        }


    });

});


/**
 * 과목 별 공부시간 순위 조회
 */
app.post('/categoryChart', (req, res) => {

    var today = getToday().substring(0, 10)

    var sql = "select sum(studytime.fullTime)'categoryFull',studytime.userId,studytime.studyStart,study.category\
   from studytime left outer join study on studytime.studyId = study.studyId and studytime.userId=study.userId  group by study.userId,studytime.studyStart,study.studyId"
    connection.query(sql, function (err, result) {

        var resultCode = 404;
        var message = 'error';
        var array = new Array();

        if (err) {
            console.log(err);
        } else {
            resultCode = 200;
            message = 'search success';

            console.log(req.body.userId)

            for (var i = 0; i < result.length; i++) {



                if (result[i].studyStart.substring(0, 10) == today) {
                    array.push({ category: result[i].category, categoryFull: result[i].categoryFull })
                }

            }
        }
        res.status(200).send(JSON.stringify(array))

    })

});


/**
 * 사용자가 가입한 스터디 조회
 */
app.post('/selectStudy', (req, res) => {

    var sql = "select studyName,studyId from study where userId =" + mysql.escape(req.body.userId)


    connection.query(sql, function (err, result) {


        var array = new Array();

        if (err) {
            console.log(err);
        } else {


            for (var i = 0; i < result.length; i++) {

                array.push({ studyId: result[i].studyId, studyName: result[i].studyName })

            }
        }
        res.status(200).send(JSON.stringify(array))

    })

});


/**
 * 스터디 이름으로 스터디 아이디 조회
 */
app.post('/studyNameToId', (req, res) => {

    var sql = "select studyId from study where studyName =" + mysql.escape(req.body.studyName)

    connection.query(sql, function (err, result) {

        if (err) {
            console.log(err);
        } else {
            res.status(200).send(JSON.stringify(result[0].studyId))

        }

    })

});


/**
 * 새 장소 등록
 */
app.post('/newLocation', function (req, res) {
    console.log("/newLocation")
    console.log(req.body);

    var loCategory = req.body.loCategory;
    var loName = req.body.loName;
    var userId = req.body.userId;
    var latitude = req.body.latitude;
    var longitude = req.body.longitude;

    var sql = 'INSERT INTO location (loCategory,loName,userId,latitude,longitude) VALUES (?, ?, ?, ?, ?)';

    var params = [loCategory, loName, userId, latitude, longitude];

    connection.query(sql, params, function (err, result) {
        var resultCode = 404;
        var message = 'error';

        if (err) {
            console.log(err);
        } else {
            resultCode = 200;
            message = 'sign up success';
        }

        res.json({
            'code': resultCode,
            'message': message
        });
    });

});

/**
 * 내 장소 조회
 */
app.post('/locationSearch', (req, res) => {

    var sql = "select loName, latitude, longitude from location where userId=" + mysql.escape(req.body.userId);

    connection.query(sql, function (err, result) {

        var array = new Array();

        if (err) {
            console.log(err);
        } else {

            for (var i = 0; i < result.length; i++) {
                array.push({ loName: result[i].loName, latitude: result[i].latitude, longitude: result[i].longitude });

            }
        }
        res.status(200).send(JSON.stringify(array))

    })

});

/**
 * 스터디 탈퇴
 */
app.post('/studyOut', function (req, res) {

    var sql = "select studyId from study where studyName =" + mysql.escape(req.body.studyName) + " and userId=" + mysql.escape(req.body.userId);
    connection.query(sql, function (err, result) {
        if (err) {
            console.log(err);
        } else {

            var sql1 = "delete from study where studyId =" + result[0].studyId + " and userId=" + mysql.escape(req.body.userId);

            connection.query(sql1, function (err, result1) {
                var resultCode = 404;
                var message = 'error';
                var sql2 = "delete from studytime where studyId =" + result[0].studyId + " and userId=" + mysql.escape(req.body.userId);
                if (err) {
                    console.log(err);
                } else {
                    resultCode = 200;
                    message = 'study out success';

                    connection.query(sql2, function (err, result2) {

                        var resultCode = 404;
                        var message = 'error';

                        if (err) {
                            console.log(err);
                        } else {

                            console.log(result2);
                            resultCode = 200;
                            message = 'studytime out success';
                            console.log(message);
                        }

                    });

                }

                res.json({
                    'code': resultCode,
                    'message': message
                });
            });


        }


    });

});



/**
 * 주간 공부시간 조회
 */
app.post('/weeklyChart', function (req, res) {


    var today = getToday().substring(0, 10)
    var weekNum = new Date(today).getDay();
    var startOfWeek
    var endOfWeek


    switch (weekNum) {
        case 0:

            startOfWeek = new Date(today).getDate() - 6
            endOfWeek = today
            break;
        case 1:

            startOfWeek = today
            endOfWeek = new Date(today).getDate() + 6
            break;
        case 2:

            startOfWeek = new Date(today).getDate() - 1
            endOfWeek = new Date(today).getDate() + 5
            console.log(startOfWeek)
            console.log(endOfWeek)

            break;
        case 3:

            startOfWeek = new Date(today).getDate() - 2
            endOfWeek = new Date(today).getDate() + 4
            break;
        case 4:
            startOfWeek = new Date(today).getDate() - 3
            endOfWeek = new Date(today).getDate() + 3
            break;

        case 5:
            startOfWeek = new Date(today).getDate() - 4
            endOfWeek = new Date(today).getDate() + 2
            break;
        case 6:
            startOfWeek = new Date(today).getDate() - 5
            endOfWeek = new Date(today).getDate() + 1
            break;
        default:
    }

    var m = new Date().getMonth() + 1
    var mon
    var array = new Array();
    var x = startOfWeek

    for (var i = 0; i < 7; i++) {

        var sql

        if (x > 30) {
            x = 1
            mon++
        }
        /*몇월몇일에서 일이 한자리수일때와 두자리수일때 sql문 다르게 보냄*/
        if (x < 10) {

            if (m < 10) {

                sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" + mysql.escape(req.body.userId) +
                    "and studyStart like '2020-0" + m + "-0" + x + "%' "

            } else {

                sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" + mysql.escape(req.body.userId) +
                    "and studyStart like '2020-" + m + "-0" + x + "%' "

            }
        } else {
            if (m < 10) {
                sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" + mysql.escape(req.body.userId) +
                    "and studyStart like '2020-0" + m + "-" + x + "%' "


            } else {


                sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" + mysql.escape(req.body.userId) +
                    "and studyStart like '2020-" + m + "-" + x + "%' "
            }


        }

        connection.query(sql, function (err, result) {

            if (err) {
                console.log(err);
            } else {
                array.push({ fullTime: result[0].fulltime, day: String(result[0].studyStart).substring(8, 10) })

                for (var k in array) {


                    if (k == 6) {

                        res.status(200).send(JSON.stringify(array))


                    }


                }

            }

        });
        x = x + 1

    }

});

/**
 * 집중도 순위 백분율 구하기
 */
app.post('/attentionRanking', function (req, res) {

    var array = new Array();

    var sql = "select userId, sum(greenTime)/sum(fullTime)*100 as 'attention',\
        rank() over (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'rank'\
        from studyTime group by userId;";

    connection.query(sql, function (err, result) {

        var resultCode = 404;
        var rank;
        var order;
        var count;
        var upDown;

        if (err) {
            console.log(err);
        } else {
            resultCode = 200;

            for (var i = 0; i < result.length; i++) {

                if (result[i].userId == req.body.userId) {
                    order = i;
                    count = result.length;
                    rank = result[i].rank;
                    var percent = (rank / count * 100).toFixed(0);

                    if (percent >= 50) {
                        upDown = "하위"

                        if (100 - percent == 0) {

                            array.push({ upDown: upDown, percentage: (100 - percent + 1).toString() })

                        }
                        else {

                            array.push({ upDown: upDown, percentage: (100 - percent).toString() })

                        }

                    } else {
                        upDown = "상위"

                        array.push({ upDown: upDown, percentage: percent.toString() })
                    }

                }
            }
        }

        res.status(200).send(JSON.stringify(array))
    });

});


/**
 * 집중 효율 높은 장소 추천
 */
app.post('/PlaceRecommend', function (req, res) {


    var array = new Array();
    var send = new Array();

    var ok = 0;

    var loNameSearch = "select distinct studytime.latitude,location.loName from studytime join location on studytime.latitude = location.latitude where studytime.userid=" + mysql.escape(req.body.userId)

    connection.query(loNameSearch, function (err, result) {

        if (err) {
            console.log(err);
        } else {

            for (var i = 0; i < result.length; i++) {

                array.push(result[i].loName)

                var sql = "select studytime.userId as userId,sum(studytime.greentime) greentime,sum(studytime.fulltime) fulltime,studytime.latitude as latitude,studytime.longitude as longitude,location.loCategory as loCategory from studytime left outer join location on studytime.latitude =location.latitude where location.loName='"
                    + result[i].loName + "' and studytime.userId =" + mysql.escape(req.body.userId)

                connection.query(sql, function (ERR, RESULT) {

                    if (ERR) {

                        console.log(ERR);
                    } else {
                        send.push({ loCategory: RESULT[0].loCategory, fulltime: RESULT[0].fulltime, greentime: RESULT[0].greentime })

                        ok++
                    }
                    if (ok == result.length) {
                        res.status(200).send(JSON.stringify(send))
                    }
                });
            }

        }


    });


});


/**
 * 나의 스터디 리스트 조회
 */
app.post('/myStudyList', function (req, res) {

    var send = Array();
    console.log("/myStudyList 들어옴")
    var sql = "select distinct studytime.studyId as studyId,study.studyName as studyName from studytime left outer join study on studytime.studyId = study.studyId where studytime.userId=" + mysql.escape(req.body.userId)


    connection.query(sql, function (err, result) {
        if (err) {

        } else {

            for (var i = 0; i < result.length; i++) {

                console.log("결과 :", result[i].studyId, result[i].studyName)

                send.push({ studyId: result[i].studyId, studyName: result[i].studyName })
            }

            res.status(200).send(JSON.stringify(send))
        }

    });
});


/**
 * 과목별 집중도 조회
 */
app.post('/attentionCategory', (req, res) => {

    console.log("UserId :", req.body.userId);
    var userId = req.body.userId;
    var upDown;

    var sql1 = " select distinct studytime.userId, study.category as 'myCategory'\
    from studytime left outer join study on studytime.studyId = study.studyId \
    and studytime.userId = study.userId where study.userId = '"+ userId + "'"


    connection.query(sql1, function (err, result1) {

        var array = new Array();

        if (err) {
            console.log(err);
        } else {

            resultCode = 200;

            for (var i = 0; i < result1.length; i++) {

                var sql2 = "select studytime.userId,sum(fullTime) as 'myFullTime',study.category as 'myCategory',\
                rank() over (order by sum(fullTime) desc) as 'rank' from studytime\
                left outer join study on studytime.studyId = study.studyId and studytime.userId = study.userId\
                where study.category ='"+ result1[i].myCategory + "'" + "group by study.userId, study.category;"

                connection.query(sql2, function (err, result2) {

                    if (err) {
                        console.log(err);
                    } else {
                        resultCode = 200;

                        for (var j = 0; j < result2.length; j++) {

                            if (result2[j].userId == userId) {

                                var categoryCount = result1.length;
                                var count = result2.length;
                                var rank = result2[j].rank;
                                var percent = (rank / count * 100).toFixed(0);

                                if (percent >= 50) {
                                    upDown = "하위"
                                    if (100 - percent == 0) {
                                        array.push({ category: result2[j].myCategory, upDown: upDown, percentage: (100 - percent + 1).toString() })
                                    }
                                    else {

                                        array.push({ category: result2[j].myCategory, upDown: upDown, percentage: (100 - percent).toString() })

                                    }

                                } else {
                                    upDown = "상위"
                                    array.push({ category: result2[j].myCategory, upDown: upDown, percentage: percent.toString() })
                                }

                                if (array.length == categoryCount) {
                                    res.status(200).send(JSON.stringify(array))
                                }
                            }

                        }
                    }

                });

            }

        }

    })

});


/**
 * 나이대 별 집중도 백분율 구하기
 */
app.post('/ageAttention', function (req, res) {

    console.log("userId :", req.body.userId);

    var array = new Array();

    var age;
    var group;

    ageSql = "select age from users where userId='" + req.body.userId + "'"

    console.log(ageSql)

    connection.query(ageSql, function (err, ageResult) {

        if (err) {
            console.log(err);
        } else {
            age = ageResult[0].age

            if (age < 8) {
                group = "유아"
                var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
    (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
    left outer join users on users.userId =studytime.userId\
    where users.age <8\
    group by users.userId;"

            }

            else if (age < 14) {
                group = "초등학생"
                var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age <14\
        group by users.userId;"
            } else if (age < 17) {

                group = "중학생"
                var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age BETWEEN 14 and 17;\
         group by users.userId;";

            } else if (age < 20) {
                group = "고등학생"
                var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age BETWEEN 17 and 19;\
         group by users.userId;";

            } else {
                group = "성인"
                var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age  >= 20\
         group by users.userId;";

            }


            connection.query(sql, function (err, result) {

                var resultCode = 404;
                var rank;
                var order;
                var count;

                if (err) {
                    console.log(err);
                } else {
                    resultCode = 200;
                    for (var i = 0; i < result.length; i++) {

                        if (result[i].userId == req.body.userId) {
                            order = i;
                            count = result.length;
                            rank = result[i].agerank;

                            var percent = (rank / count * 100).toFixed(0);

                            if (percent >= 50) {
                                upDown = "하위"

                                if (100 - percent == 0) {
                                    array.push({ group: group, upDown: upDown, percent: (100 - percent + 1).toString() })
                                } else {
                                    array.push({ group: group, upDown: upDown, percent: (100 - percent).toString() })
                                }

                            } else {
                                upDown = "상위"

                                array.push({ group: group, upDown: upDown, percent: percent.toString() })

                            }
                        }
                    }
                }

                res.status(200).send(JSON.stringify(array))
            });

        }
    });


});




/**
 * 오늘 총 공부시간 조회
 */
app.post('/todayStudy', (req, res) => {

    var today = getToday().substring(0, 10);
    var userId = req.body.userId;
    var array = new Array();

    var sql = "select sum(fullTime) as 'todayTime', studyStart\
   from studytime where studyStart like '"+ today + "%' and userId = '" + userId +
        "' group by userId;"

    connection.query(sql, function (err, result) {

        var resultCode = 404;
        var message = 'error';


        if (err) {
            console.log(err);
        } else {
            resultCode = 200;
            message = 'search success';

            for (var i = 0; i < result.length; i++) {

                var second = result[i].todayTime
                var minute = second / 60
                var hour = minute / 60
                second = second % 60
                minute = minute % 60

                array.push({ hour: Math.floor(hour).toFixed(0).toString(), minute: Math.floor(minute).toFixed(0).toString() })

            }
        }
        res.status(200).send(JSON.stringify(array))

    })

});







