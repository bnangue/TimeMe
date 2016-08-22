 <?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");

if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

 $title=$_POST["title"];
   $description=$_POST["description"];
    $creator=$_POST["creator"];   
   $datetime=$_POST["datetime"];
  $category=$_POST["category"];   
   $startingtime=$_POST["startingtime"];
   $endingtime=$_POST["endingtime"];
   $hashid=$_POST["hashid"];
  
   
   $statement= "INSERT INTO CalenderEvents(title ,description , datetime ,creator,category,startingtime,endingtime,hashid)
    VALUES('$title','$description','$datetime','$creator','$category','$startingtime','$endingtime','$hashid') ON DUPLICATE KEY 
    UPDATE title= '$title', description='$description', datetime='$datetime',startingtime='$startingtime', endingtime='$endingtime'";
  
	  if(mysqli_query($con, $statement)){
    echo "Event added successfully";
  }else{
    echo "Error adding Event";
  }
   
   mysqli_close($con);

?>