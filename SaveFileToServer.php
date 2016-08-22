 <?php
$con=mysqli_connect("mysql1.000webhost.com","a9359727_brice","fXu*br5q","a9359727_time");

if(!$con){
  die("Connection failed: " . mysqli_connect_error());
}

 $file=$_POST["file"];
   
   $statement= "INSERT INTO ItemTable (file) VALUES('$file') ON DUPLICATE KEY 
    UPDATE file= '$file'";
  
    if(mysqli_query($con, $statement)){
    echo "file added successfully";
  }else{
    echo "file adding Event";
  }
   
   mysqli_close($con);

?>