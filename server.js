var mysql = require('mysql');
var express = require('express');
var bodyParser = require('body-parser');
var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(express.json())


//바뀜@@@@@
var x;
var STUDYSTART;

app.listen(3000, function () {
    console.log('start...');
});

var connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    database: 'test',
    password: '3322',
});
connection.connect(function(err){  
    if(!err) {  
        console.log("Database is connected ... \n\n");    
    } else {  
        console.log("Error connecting database ... \n\n");    
    }  
    });  


 var studyId;
 var greenTime;
 var fullTime;
 var userId;
 var attention;
 var startTime;
 var endTime;
 


var value;

function dateParse(str){
    var year = str.substring(0, 4);
    var month = str.substring(5, 7);
    var day = str.substring(8, 10);
    var hour = str.substring(11, 13);
    var minute = str.substring(14, 16);
    var second = str.substring(17, 19);
    
    var a= new Date(year, month, day, hour, minute, second);
    return a
}

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
  
  function leadingZeros(n, digits) {
    var zero = '';
    n = n.toString();
  
    if (n.length < digits) {
      for (i = 0; i < digits - n.length; i++)
        zero += '0';
    }
    return zero + n;
  }

app.post('/GroupChart',(req,res)=>{
 

    
    console.log("조회하려는 studyId :",req.body.studyId)
    
    
var sqlChart="select sum(greenTime) sumgreenTime, userId from studytime where studyId="+mysql.escape(req.body.studyId)+"  group by userId  "

connection.query(sqlChart,function(err,rows,fields){

    var arr=new Array()
    if (!err){  

        console.log(sqlChart)
        console.log('성공 : ');  
       console.log("쿼리결과 :"+rows)

        for(var i=0;i<rows.length;i++){
            arr.push({greenTime: rows[i].sumgreenTime,userId: rows[i].userId})
             
            


         }
         console.log(arr)
         console.log(JSON.stringify(arr))
        // res.status(200).send(JSON.stringify(arr))//한번만 써야함
        res.status(200).send(JSON.stringify(arr))//한번만 써야함
        

       // console.log(arr)
       
      
       
        
      }  
      else  {
        console.log('Error while performing Query.');  
        console.log(err)

      }

});
  




})

app.post('/userIdsearch',(req,res)=>{
 

   
    console.log("검색스토디Id :",req.body.studyId)
    console.log("userId :",req.body.userId)

    var sqlSearch="SELECT DISTINCT userId from studytime WHERE studyId ="+mysql.escape(req.body.studyId)+
    "AND userId !=" +mysql.escape(req.body.userId)+"AND studyEnd IS NOT NULL "

    connection.query(sqlSearch,function(err,rows,fields){

        if (!err){  
            console.log('빨간불 user : ');  
           
            var arr=new Array()


            for(var i=0;i<rows.length;i++){
               /* console.log("studyStart ",rows[i].studyStart)
                console.log("studyEnd ",rows[i].studyEnd)
                console.log("userId ",rows[i].userId)*/
                
                arr.push({userId: rows[i].userId})


            }
    
            console.log(JSON.stringify(arr))
           // res.status(200).send(JSON.stringify(arr))//한번만 써야함
           res.status(200).send(JSON.stringify(arr))//한번만 써야함
           

           
           
          
           
            
          }  
          else  {
            console.log('Error while performing Query.');  
            console.log(err)

          }

   
   
        });

    






  



})

app.post('/Greensearch',(req,res)=>{
 

    console.log("조회 시각 :",req.body.currentTime)
    console.log("스토디Id :",req.body.studyId)
    console.log("userId :",req.body.userId)

    //let arr=new Array()
    



    console.log(dateParse(req.body.currentTime))


    var sqlSearch="SELECT studyStart,studyEnd,userId from studytime WHERE studyId ="+mysql.escape(req.body.studyId)

    connection.query(sqlSearch,function(err,rows,fields){

        var arr=new Array()
        if (!err){  
            console.log('성공 : ');  
           

            for(var i=0;i<rows.length;i++){
                /* console.log("studyStart ",rows[i].studyStart)
                 console.log("studyEnd ",rows[i].studyEnd)
                 console.log("userId ",rows[i].userId)*/
                 if(req.body.userId!=rows[i].userId){
                   
         

                    if(dateParse(req.body.currentTime)>dateParse(rows[i].studyStart)){
                       //다른 스터디원이 먼저 공부중일때
    
                       
                       if(!rows[i].studyEnd){
    
                        
                        arr.push({userId: rows[i].userId})
                       
                       // res.status(200).send(JSON.stringify({userId : rows[i].userId}))//한번만 써야함
    
    
                       }
    
                    }
                }
                
 
 
             }
             console.log(arr)
             console.log(JSON.stringify(arr))
            // res.status(200).send(JSON.stringify(arr))//한번만 써야함
            res.status(200).send(JSON.stringify(arr))//한번만 써야함
            

           // console.log(arr)
           
          
           
            
          }  
          else  {
            console.log('Error while performing Query.');  
            console.log(err)

          }

    });


})


app.post('/StudySearch',(req,res)=>{
 

    console.log("스터디 이름 조회 :",req.body.studyName)
  
    //let arr=new Array()


   // var sqlSearch="SELECT studyStart,studyEnd,userId from studytime WHERE studyId ="+mysql.escape(req.body.studyId)

   var sqlSearch="select studyName from study where studyName like "+mysql.escape('%'+req.body.studyName+'%')+"group by studyId"
    connection.query(sqlSearch,function(err,rows,fields){

        console.log("쿼리 :",sqlSearch)

        var arr=new Array()
        if (!err){  
            console.log('쿼리결과 : ');  
            console.log(rows)
           

            
            for(var i=0;i<rows.length;i++){

                arr.push({studyName: rows[i].studyName})
                
               
 
 
             }
             console.log("보낼데이터 :\n",arr)
       
            // res.status(200).send(JSON.stringify(arr))//한번만 써야함
            res.status(200).send(JSON.stringify(arr))
            
           
          
           
            
          }  
          else  {
            console.log('Error while performing Query.');  
            console.log(err)

          }

    });


})

app.post('/test', (req, res) => {
    console.log("trackActivity에서 공부방 입장 ")

            const newUser = {
                studyId: req.body.studyId,
                greenTime: req.body.greenTime,
                fullTime: req.body.fullTime,
                userId: req.body.userId,
                startTime: req.body.startTime,
                endTime: req.body.endTime
               
            }
            

            console.log(" 성공적으로 데이터 받음 studyId :"+newUser.studyId)
            /*
            console.log(" greenTime :"+newUser.greenTime)
            console.log(" fullTime :"+newUser.fullTime)
            console.log(" userId :"+newUser.userId)
            console.log(typeof newUser.studyId)
            */

            studyId=parseInt(newUser.studyId)
            greenTime=parseInt(newUser.greenTime)
            fullTime=parseInt(newUser.fullTime)
            userId=newUser.userId
            attention=(greenTime/fullTime).toFixed(2)
            console.log("attention"+attention)
            startTime=newUser.startTime
            endTime=newUser.endTime
//바뀜@@@@@
            STUDYSTART=startTime

           
         

             if (newUser.studyId == null) {
                   res.status(400).send()
                      
                
            } else {
                    res.status(200).send()
            }

            


            
       var sqlInsert= "INSERT INTO studytime VALUES ?"

       

       var sqlSelect="SELECT studyId from studytime WHERE studyId = ?"

       

       console.log("스터디id"+studyId)


     

        //attention 빼기

          var sql="INSERT INTO studytime VALUES "+"("+ mysql.escape(studyId) +","+mysql.escape(greenTime) +
          ","+ mysql.escape(fullTime)+","+mysql.escape(userId)+","+mysql.escape(startTime)+","+ mysql.escape(endTime)+","+mysql.escape(req.body.latitude)+","+mysql.escape(req.body.longitude)+")"
           console.log(sql)
            connection.query(sql,function(err,rows,fields){

                if (!err){  
                    console.log('성공 : ', rows);  
                  }  
                  else  
                    console.log('Error while performing Query.업데이트오류');  
                    console.log(err)

            });





/*

          var sql="UPDATE `test`.`location` SET `studyStart` ="+mysql.escape(startTime)+
          " WHERE (`userId` = "+mysql.escape(userId)+")" 
          

 
           console.log(sql)
            connection.query(sql,function(err,rows,fields){

                if (!err){  
                    console.log('location 테이블에 studyStart 넣기 성공 : ', rows);  
                  }  
                  else  
                    console.log('Error while performing Query.업데이트오류');  
                    console.log(err)

            });
          

    */

    




        });  

app.post('/testEnd', (req, res) => {

            const newUser = {
                studyId: req.body.studyId,
                greenTime: req.body.greenTime,
                fullTime: req.body.fullTime,
                userId: req.body.userId,
                startTime: req.body.startTime,
                endTime: req.body.endTime
               
            }
            /*

            console.log(" 성공적으로 데이터 받음 studyId :"+newUser.studyId)
            console.log(" greenTime :"+newUser.greenTime)
            console.log(" fullTime :"+newUser.fullTime)
            console.log(" userId :"+newUser.userId)
            console.log(typeof newUser.studyId)
            */

            studyId=parseInt(newUser.studyId)
            greenTime=parseInt(newUser.greenTime)
            fullTime=parseInt(newUser.fullTime)
            userId=newUser.userId
            attention=(greenTime/fullTime).toFixed(2)
            console.log("attention"+attention)
            startTime=newUser.startTime
            endTime=newUser.endTime

           
         

             if (newUser.studyId == null) {
                   res.status(400).send()
                      
                
            } else {
                    res.status(200).send()
            }

            



       
//attention빼기
       console.log("스터디id"+studyId)

            var sql2="UPDATE studytime SET greenTime="+mysql.escape(greenTime) + ", fullTime ="+mysql.escape(fullTime) +
             ",studyEnd ="+mysql.escape(endTime) + "where studyStart ='"+STUDYSTART+"'"
            console.log(sql2)

     
//바뀜@@@@@
        

            connection.query(sql2,function(err,rows,fields){

                if (!err){  
                    console.log('성공 : ', rows);  
                  }  
                  else  
                    console.log('Error while performing Query.업데이트오류');  
                    console.log(err)

            });


        });  

        app.post('/resisterStudy', function (req, res) {

 
    
 
 
            var sql = "select studyId, startDate,studyName,category,endDate  from study where studyName ="+mysql.escape(req.body.studyName)+""
        
            connection.query(sql, function (err, result) {
               
        
                if (err) {
                    console.log(err);
        
                } else {
                    console.log(result[0])
                   
                   var insrt="INSERT INTO `test`.`study` (`studyId`, `startDate`, `studyName`, `userId`, `category`, `endDate`) VALUES (?, ?, ?, ?, ?,?)"
                   var params = [result[0].studyId,result[0].startDate,result[0].studyName,req.body.userId,result[0].category,result[0].endDate];
               
                 
               
                   connection.query(insrt, params, function (err, result2) {
                       
               
                       if (err) {
                           if(err.errno==1062){
                               res.status(400).send()
                           }
                           
                          
                          
                       } else {
                           res.status(200).send()
       
       
                           
                       }
               
                      
                   });
        
                  
        
                }
        
               
            });
        
        });

        app.post('/register', function (req, res) {
            console.log(req.body);
            var userid = req.body.userid;
            var password = req.body.password;
            var age = req.body.age;
            var gender = req.body.gender;
        
        
            var sql = 'INSERT INTO users (userid,password,age,gender) VALUES (?, ?, ?, ?)';
            var params = [userid,password,age,gender];
        
          
        
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
            studyId = parseInt(result[0].last) + 1 ;
            console.log(studyId)

            var sql2 = 'INSERT INTO study (category,startDate,endDate,studyName,userId,studyId) VALUES (?, ?, ?, ?, ?, ?)';
         
            var params = [category,startDate,endDate,studyName,userId,studyId];

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


/*
 추가
 */
app.post('/enterStudy', function (req, res) {

   // console.log(req.body);
   


   var arr = new Array();
    var sql = "select study.category, study.startDate,study.endDate,count(distinct study.userId) as member from study left outer join  studytime on study.studyId=studytime.studyId where study.studyName ="+mysql.escape(req.body.studyName)+""
//조인 안해서 그런거
    connection.query(sql, function (err, result) {
       

        if (err) {
            console.log(err);

        } else {
           
            console.log("일단여기")
            console.log(JSON.stringify(result[0]))
            const newUser = {
                category: result[0].category,
                startDate: result[0].startDate ,
                endDate: result[0].endDate,
                member: String(result[0].member)
            }
            console.log(newUser)

            res.status(200).send(JSON.stringify(result[0]))

          

        }

       
    });

});


app.post('/resisterStudy', function (req, res) {

 
    
 
 
     var sql = "select studyId, startDate,studyName,category,endDate  from study where studyName ="+mysql.escape(req.body.studyName)+""
 
     connection.query(sql, function (err, result) {
        
 
         if (err) {
             console.log(err);
 
         } else {
             console.log(result[0])
            
            var insrt="INSERT INTO `test`.`study` (`studyId`, `startDate`, `studyName`, `userId`, `category`, `endDate`) VALUES (?, ?, ?, ?, ?,?)"
            var params = [result[0].studyId,result[0].startDate,result[0].studyName,req.body.userId,result[0].category,result[0].endDate];
        
          
        
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



app.post('/greenByFull', function (req, res) {

 
    
 console.log("일일 집중대비 공부시간  조회 요청 시각:"+getToday())
 var today=getToday().substring(0,10)//yyyy-mm-dd 까지
 var tomorow=getNextday().substring(0,10)

 // 오늘 날짜와 다음날짜 00시~02시 대 인것들만 셀렉트
 
    var sql = "select greenTime, fullTime ,studyStart from studytime where userId="+mysql.escape(req.body.userId)+" or studyStart like "
    +mysql.escape('%'+today+'%')+"and studyStart like"+mysql.escape('%'+tomorow+' 00'+'%')+mysql.escape('%'+today+'%')+"and studyStart like"
    +mysql.escape('%'+tomorow+' 01'+'%')+mysql.escape('%'+today+'%')+"and studyStart like"+mysql.escape('%'+tomorow+' 02'+'%')

    connection.query(sql, function (err, result) {

        console.log(sql)

        var array= new Array();

        if (err) {
            console.log(err);

        } else {
          //  console.log(sql)

            for(var i=0;i<result.length;i++){
                

                
                if(result[i].studyStart.substring(0,10)==today&&parseInt(result[i].studyStart.substring(11,13))>2){
                    //오늘날짜이고 03시~23시 대
                    array.push(
                        {time:result[i].studyStart.substring(11,16),
                    green:Math.round(result[i].greenTime/60),full:Math.round(result[i].fullTime/60)
                         })

                         console.log(result[i].studyStart.substring(11,16),
                        Math.round(result[i].greenTime/60),Math.round(result[i].fullTime/60))
                    
                }

                if(result[i].studyStart.substring(0,10)==tomorow&&parseInt(result[i].studyStart.substring(11,13))<3){
                    //다음날짜이고 00시~02시 대

                    array.push(
                        {time:result[i].studyStart.substring(11,16),
                    green:Math.round(result[i].greenTime/60),full:Math.round(result[i].fullTime/60)
                         })
                         console.log(result[i].studyStart.substring(11,16),
                         Math.round(result[i].greenTime/60),Math.round(result[i].fullTime/60))
                    
                }
                
           
         
           

          

            }

            console.log("결과 :",array)
            res.status(200).send(JSON.stringify(array))

        }

       
    });

});



app.post('/attentionMinMax', function (req, res) {
    //var first=0

  
    console.log(req.body.min,req.body.max);
    var select="select userId from attentionface"
    connection.query(select,function(error,result2){
       
        if(!error){
            console.log('/attentionMinMax 1');

            for(var i=0;i<result2.length;i++){
                console.log('/attentionMinMax 2');

                if(result2[i].userId==req.body.userId){
                    first=0
                    console.log("조회 결괴"+result2[i].userId)
                

                    var update="UPDATE attentionFace SET min = "+mysql.escape(parseFloat(req.body.min))+
                    ",  max ="+mysql.escape(parseFloat(req.body.max))+" WHERE userId = "
                    +mysql.escape(req.body.userId) 
                

                    console.log("업뎃쿼리 :"+update)
                
                        connection.query(update,function(error2,reslut3){

                    
                        if (error2) {
                            console.log(error2);
            
                        } else {
                      //  console.log(sql)
            
                        
            
                            //console.log("이미 등록된 사용자 재등록 결과 :",reslut3)
                      
            
                        }

                 

                    });
                
               

                }else{


                first=1
                console.log('/attentionMinMax 3');
               
                }
            

            }


            if(first==1){
                console.log('/attentionMinMax 4');
                console.log(req.body.userId,req.body.min,req.body.max,"를 받음")


                var sql ="INSERT INTO attentionface VALUES "+"("+ mysql.escape(req.body.userId) +","+mysql.escape(parseFloat(req.body.min)) +
                ","+ mysql.escape(parseFloat(req.body.max))+")"
            
                connection.query(sql, function (err, result) {
                   
                    var array= new Array();
            
                    if (err) {
                        console.log(err);
            
                    } else {

                        
            
                        console.log("최초 등록 결과 :",result)
                      
            
                    }
            
                   
                });


            }
          

            res.status(200).send()
        }


    })
    
    
   });

   
app.post('/attentionChk', function (req, res) {

 console.log("/attentionChk")
    
    
    var sql ="select  min,max from attentionFace where userId ="+mysql.escape(req.body.userId)

    connection.query(sql, function (err, result) {
       
       

        if (err) {
            console.log(err);

        } else {
          //  console.log(sql)

            

          console.log("결과 :",JSON.stringify(result[0].min+","+result[0].max))
         //   console.log("결과 :",result)
            res.status(200).send(JSON.stringify(result[0].min+","+result[0].max))

        }

       
    });

});


app.post('/categoryChart',(req, res)=> {

    var today=getToday().substring(0,10)

   // var sql ="select sum(studytime.fullTime)'categoryFull',studytime.userId,studytime.studyStart,study.category\
   // from studytime left outer join study on studytime.studyId = study.studyId group by study.category"

   var sql ="select sum(studytime.fullTime)'categoryFull',studytime.userId,studytime.studyStart,study.category\
   from studytime left outer join study on studytime.studyId = study.studyId and studytime.userId=study.userId  group by study.userId,studytime.studyStart,study.studyId"
    connection.query(sql,function(err,result){

        var resultCode = 404;
        var message = 'error';
        var array = new Array();
   
        if (err) {
            console.log(err);
        } else {
            resultCode = 200;
            message = 'search success';

            console.log(req.body.userId)

            for(var i=0;i<result.length;i++){

                

                if(result[i].studyStart.substring(0,10)==today){

                    /*

                    for(var j = 0; j<result.length; j++){
                        if(result[j])
                    }

                    */
          
                    array.push({category: result[i].category,categoryFull:result[i].categoryFull})
                        
                }
    
            }
        }
        console.log("array: "+array)
        console.log(JSON.stringify(array))
        res.status(200).send(JSON.stringify(array))
           
    })

});



app.post('/selectStudy',(req, res)=> {

   

    var sql ="select studyName,studyId from study where userId ="+mysql.escape(req.body.userId)


    connection.query(sql,function(err,result){

      
        var array = new Array();
   
        if (err) {
            console.log(err);
        } else {
           

            for(var i=0;i<result.length;i++){

              
          
                    array.push({studyId: result[i].studyId,studyName: result[i].studyName})
          
    
            }
        }
        console.log("array: "+array)
        console.log(JSON.stringify(array))
        res.status(200).send(JSON.stringify(array))
           
    })

});

app.post('/studyNameToId',(req, res)=> {

   

    var sql ="select studyId from study where studyName ="+mysql.escape(req.body.studyName)


    connection.query(sql,function(err,result){

      
        var array = new Array();
   
        if (err) {
            console.log(err);
        } else {
           
            console.log("결과 스터디 아디: "+result[0].studyId)
        
            res.status(200).send(JSON.stringify(result[0].studyId))
               
       
           
        }
       
    })

});

app.post('/newLocation', function (req, res) {
console.log("/newLocation")
    console.log(req.body);

    var loCategory = req.body.loCategory;
    var loName = req.body.loName;
    var userId = req.body.userId;
    var latitude = req.body.latitude;
    var longitude = req.body.longitude;
    var studyStart=req.body.studyStart;

    var sql = 'INSERT INTO location (loCategory,loName,userId,latitude,longitude) VALUES (?, ?, ?, ?, ?)';

    var params = [loCategory,loName,userId,latitude,longitude];

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

    app.post('/locationSearch',(req, res)=> {


        var sql ="select loName, latitude, longitude from location where userId="+mysql.escape(req.body.userId);
    
    
        connection.query(sql,function(err,result){
    
          
            var array = new Array();
       
            if (err) {
                console.log(err);
            } else {
               
    
                for(var i=0;i<result.length;i++){
    
                
                        array.push({loName: result[i].loName,latitude: result[i].latitude,longitude: result[i].longitude});
              
                }
            }
            console.log("array: "+array)
            console.log(JSON.stringify(array))
            res.status(200).send(JSON.stringify(array))
               
        })
    
    });

    app.post('/studyOut', function (req, res) {
        console.log(req.body);

        var sql = "select studyId from study where studyName ="+mysql.escape(req.body.studyName)+" and userId="+mysql.escape(req.body.userId);
        connection.query(sql, function (err, result) {


            if (err) {
                console.log(err);
            } else {
                console.log(result);
                var sql1 = "delete from study where studyId ="+result[0].studyId+" and userId="+mysql.escape(req.body.userId);

                connection.query(sql1, function (err, result1) {
                    var resultCode = 404;
                    var message = 'error';
                    var sql2 = "delete from studytime where studyId ="+result[0].studyId+" and userId="+mysql.escape(req.body.userId);
                    if (err) {
                        console.log(err);
                    } else {
                        resultCode = 200;
                        message = 'study out success';
                        console.log(message);
                        console.log(result1);
        
                        connection.query(sql2, function (err, result2) {
                            
                            var resultCode = 404;
                            var message = 'error';
        
                            if(err){
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


    

app.post('/weeklyChart', function (req,res){
    

    var today=getToday().substring(0,10)


    var weekNum=new Date(today).getDay();
    var startOfWeek
    var endOfWeek
    
    
    switch (weekNum){
        case 0 :
        
          startOfWeek=new Date(today).getDate()-6
          endOfWeek=today
        
    
            break;
        case 1 :
          
          startOfWeek=today
          endOfWeek=new Date(today).getDate()+6
            break;
        case 2 :
      
         startOfWeek=new Date(today).getDate()-1
         endOfWeek=new Date(today).getDate()+5
         console.log(startOfWeek)
         console.log(endOfWeek)
    
            break;
        case 3 :
          
          startOfWeek=new Date(today).getDate()-2
          endOfWeek=new Date(today).getDate()+4
            break;
        case 4 :
          startOfWeek=new Date(today).getDate()-3
          endOfWeek=new Date(today).getDate()+3
            break;
            
        case 5 :
         startOfWeek=new Date(today).getDate()-4
         endOfWeek=new Date(today).getDate()+2
              break;
        case 6 :
         startOfWeek=new Date(today).getDate()-5
         endOfWeek=new Date(today).getDate()+1
              break;
        default :
    }
    
    

    console.log("/weeklyChart 들어옴")
    console.log(startOfWeek)

    var m=new Date().getMonth()+1
    var mon
    


    var array = new Array();

    var x=startOfWeek
    var json


    
    
 for(var i=0;i<7;i++){
     
    var sql

    if(x>30)
    {

        x=1
        mon++


    }
/*몇월몇일에서 일이 한자리수일때와 두자리수일때 sql문 다르게 보냄*/ 
    if(x<10){

        if(m<10){

            sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" +mysql.escape(req.body.userId) +
            "and studyStart like '2020-0"+m+"-0"+x+"%' "  

        }else{

            sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" +mysql.escape(req.body.userId) +
            "and studyStart like '2020-"+m+"-0"+x+"%' "  

        }




    }else{
        if(m<10){
            sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" +mysql.escape(req.body.userId) +
            "and studyStart like '2020-0"+m+"-"+x+"%' "  


        }else{


            sql = "select sum(fullTime) as fulltime,studyStart from studytime where userId=" +mysql.escape(req.body.userId) +
            "and studyStart like '2020-"+m+"-"+x+"%' "  
        }
      

    }
    
   

    console.log(i+"번쩨"+sql)

    




    connection.query(sql, function(err,result) {

        if(err) {
            console.log(err);
        }else {

            
           // console.log("여기"+typeof(String(result[0].studyStart)))
                //console.log(result[0].fulltime,result[0].studyStart)
               
               array.push({fullTime: result[0].fulltime,day:String(result[0].studyStart).substring(8,10)})
              
 

       


            
           
           

               for(var k in array){
                  

                if(k==6){

                    res.status(200).send(JSON.stringify(array))

                 
                }

            
            }
           // array.push({fullTime: result[0].fulltime,day:result[0].studyStart})
        
            }
    
        
    });

           
    x=x+1
   
 
     

 }

    





});

app.post('/attentionRanking', function (req, res) {

        console.log("UserId :",req.body.userId);

        var array = new Array();
       
        var sql ="select userId, sum(greenTime)/sum(fullTime)*100 as 'attention',\
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

                for(var i = 0; i<result.length; i++){

                    if (result[i].userId ==req.body.userId)

                    {

                        order = i;
                        count = result.length;
                        rank = result[i].rank;
                        var percent = (rank/count*100).toFixed(0);

                        if(percent>=50)
                        
                        {
                            upDown = "하위"

                            if(100-percent==0){

                                array.push({upDown: upDown, percentage :(100-percent+1).toString()})

                            }
                            else{

                                array.push({upDown: upDown, percentage :(100-percent).toString()})

                            }
                            

                        }else
                        {
                            upDown = "상위"
                           
                                array.push({upDown: upDown, percentage :percent.toString()})
                           
                            
                        }
                    
                    }
                }
            }
    
            res.status(200).send(JSON.stringify(array))
            console.log(JSON.stringify(array))
            

        });
    
    
     });

     app.post('/PlaceRecommend', function (req,res){
    

        var array = new Array();
        var send= new Array();
    
        var ok=0;
    
        console.log("/PlaceRecommend 들어옴")
      
       
    var test
      
        
    
    var loNameSearch="select distinct studytime.latitude,location.loName from studytime join location on studytime.latitude = location.latitude where studytime.userid="+mysql.escape(req.body.userId)
    
    
    
        connection.query(loNameSearch, function(err,result) {
    
            if(err) {
                console.log(err);
            }else {
    
                   
                  console.log(result.length)
    
                   for(var i=0;i<result.length;i++){
                       
                    console.log("loName",result[i].loName)
                    array.push(result[i].loName)
    
                    
                    var sql ="select studytime.userId as userId,sum(studytime.greentime) greentime,sum(studytime.fulltime) fulltime,studytime.latitude as latitude,studytime.longitude as longitude,location.loCategory as loCategory from studytime left outer join location on studytime.latitude =location.latitude where location.loName='"
                    +result[i].loName+"' and studytime.userId ="+mysql.escape(req.body.userId) 
          
                              connection.query(sql,function(ERR,RESULT){
                  
                                  if(ERR){
                  
                                      console.log(ERR);
                                  }else{
                                      console.log("결과  ",RESULT[0])
                                    
                                    
    
                                      
    
                                      console.log("ok",ok)
                                      send.push({loCategory:RESULT[0].loCategory, fulltime:RESULT[0].fulltime, greentime:RESULT[0].greentime})
                                    
                                      ok++
                                      console.log("ok",ok)
                                     
    
                                   
                                    
                                  }
                                 
    
                                  if(ok==result.length){
                                      
    
    
                                    console.log("안드로 보냄",JSON.stringify(send))
                                    res.status(200).send(JSON.stringify(send))
                                  }
                                
    
                              });
    
                           
                   }
    
               
                 
                }
        
              
        });
    
       
    });

 

app.post('/myStudyList', function (req,res){

    var send=Array();
    console.log("/myStudyList 들어옴")
    var sql="select distinct studytime.studyId as studyId,study.studyName as studyName from studytime left outer join study on studytime.studyId = study.studyId where studytime.userId="+mysql.escape(req.body.userId)


    connection.query(sql, function(err,result) {
        if(err){


        }else{


            for(var i=0; i<result.length;i++){

                console.log("결과 :",result[i].studyId,result[i].studyName)

                send.push({studyId:result[i].studyId,studyName:result[i].studyName})
            }

            res.status(200).send(JSON.stringify(send))
        }

    });
});

app.post('/attentionCategory',(req, res)=> {

   

    console.log("UserId :",req.body.userId);
    var userId = req.body.userId;
    var upDown;

    var sql1=" select distinct studytime.userId, study.category as 'myCategory'\
    from studytime left outer join study on studytime.studyId = study.studyId \
    and studytime.userId = study.userId where study.userId = '"+userId+"'"


    connection.query(sql1,function(err,result1){

        var array = new Array();

        if (err) {
            console.log(err);
        } else {


            

            resultCode = 200;

            console.log(result1);

            for(var i = 0; i<result1.length; i++){

                
                var sql2 = "select studytime.userId,sum(fullTime) as 'myFullTime',study.category as 'myCategory',\
                rank() over (order by sum(fullTime) desc) as 'rank' from studytime\
                left outer join study on studytime.studyId = study.studyId and studytime.userId = study.userId\
                where study.category ='"+result1[i].myCategory+"'"+ "group by study.userId, study.category;"

                connection.query(sql2,function(err,result2){
   

            
                    if(err){
                        console.log(err);
                    }else {

                        console.log(result2);
                        resultCode=200;

                    
                        for(var j = 0; j<result2.length; j++){

                            if (result2[j].userId ==userId){

                                var categoryCount = result1.length;
                                var count= result2.length;
                                var rank= result2[j].rank;
                                var percent=(rank/count*100).toFixed(0);

                                if(percent>=50)
                        
                                {

                                    upDown = "하위"
                                    if(100-percent==0)
                                    {
                                        array.push({category: result2[j].myCategory,upDown: upDown, percentage :(100-percent+1).toString()})
                                    }
                                    else{

                                        array.push({category: result2[j].myCategory,upDown: upDown, percentage :(100-percent).toString()})

                                    }
                                    
                        
            
            
                                }else
                                {
                                    upDown = "상위"
                                

                                        array.push({category: result2[j].myCategory,upDown: upDown, percentage :percent.toString()})

                                
                                   
                                }
                                
                                if(array.length==categoryCount){

                                    res.status(200).send(JSON.stringify(array))
                                    console.log(JSON.stringify(array))
                                }

                            
    
                            }
            
                        }

              
  
                          
                       

                     }

                     
     
                });


         

           
           
          
        }

    }
           
    })

});


app.post('/ageAttention', function(req,res) {
  
    console.log("userId :",req.body.userId);

    var array = new Array();
    
    var age;
    var group;
    var updown;

    ageSql="select age from users where userId='"+req.body.userId+"'"

    console.log(ageSql)

    connection.query(ageSql, function (err, ageResult) {

        if (err) {
            console.log(err);
        } else {
            age = ageResult[0].age

            
if(age<8){
    group = "유아"
    var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
    (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
    left outer join users on users.userId =studytime.userId\
    where users.age <8\
    group by users.userId;"

}

    else if(age<14) {
        group = "초등학생"
        var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age <14\
        group by users.userId;"
    }else if(age<17){

        group = "중학생"
        var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age BETWEEN 14 and 17;\
         group by users.userId;";

    }else if(age<20){
        group = "고등학생"
        var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age BETWEEN 17 and 19;\
         group by users.userId;";

    }else{
        group = "성인"
        var sql = "select users.userId,users.age, sum(greenTime)/sum(fullTime)*100 as 'ageattention', rank() over\
        (order by sum(greenTime)/sum(fullTime)*100 DESC) as 'agerank' from studytime\
        left outer join users on users.userId =studytime.userId\
        where users.age  >= 20\
         group by users.userId;";

    }

    console.log(sql)

    connection.query(sql, function (err, result) {

        console.log("enter connection");

        var resultCode = 404;
        var rank;
        var order;
        var count;

        if (err) {
            console.log(err);
        } else {
            resultCode = 200;
            console.log(result);
            for(var i = 0; i<result.length; i++){
               
                if (result[i].userId ==req.body.userId)

                {

                    order = i;
                    count = result.length;
                    rank = result[i].agerank;

                    var percent = (rank/count*100).toFixed(0);

                    if(percent>=50)
                        
                    {
                        upDown = "하위"

                        if(100-percent==0)
                        {
                            array.push({group:group,upDown: upDown, percent :(100-percent+1).toString()})
                        }else{
                            array.push({group:group,upDown: upDown, percent :(100-percent).toString()})
                        }

                    }else
                    {
                        upDown = "상위"

                      array.push({group:group,upDown: upDown, percent :percent.toString()})
                      
                       
                    }

                
                }
            }
        }

        res.status(200).send(JSON.stringify(array))
        console.log(JSON.stringify(array))

    });

}


 });


});


    


app.post('/todayStudy',(req, res)=> {

    var today=getToday().substring(0,10);
    var userId = req.body.userId;
    var array = new Array();

   var sql ="select sum(fullTime) as 'todayTime', studyStart\
   from studytime where studyStart like '"+today+"%' and userId = '"+userId+
   "' group by userId;"

    connection.query(sql,function(err,result){

        var resultCode = 404;
        var message = 'error';
        
   
        if (err) {
            console.log(err);
        } else {
            resultCode = 200;
            message = 'search success';

            console.log(req.body.userId)

            for(var i=0;i<result.length;i++){ 
                
                var second =result[i].todayTime              
                var minute = second/60
                var hour = minute/60
                second = second%60
                minute = minute%60
                
                array.push({hour:Math.floor(hour).toFixed(0).toString(), minute:Math.floor(minute).toFixed(0).toString()})
                    
            }
        }
        console.log("array: "+array)
        console.log(JSON.stringify(array))
        res.status(200).send(JSON.stringify(array))
           
    })

});







