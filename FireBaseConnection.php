<?php

// Message to be sent
// $message = "DEMO MESSAGE";
// $registrationIDs = "APA91bF_W_zIg-T9RInVmgwLM0OkaQNsjrbJ1u9Wa6-nTEVxDqobw-YWHqFWJB1_kxgINO9RMrEJBIvd_6sSJIh7aEQyMs1THaSOFuzt9CF2eFZU9zAnqsEubYrCcRmQrFu4vv54mIkGwitIP_X_7SYkM0rMgVQWwe5z2sWqfDdspJo49vtayIY";
// $apiKey = "AIzaSyCBI_c2izVrEPcJ509uZGVLdfWUAW-rg48";
  
$message = $_POST['message'];

$sender = $_POST["sender"];
$receiver = $_POST["receiver"];

$registrationReceiverIDs = $_POST['registrationReceiverIDs'];

$registrationSenderIDs = $_POST['registrationSenderIDs'];
$title = $_POST["title"];
$chatRoom = $_POST["chatRoom"];
$senderuid=$_POST["senderuid"];

$apiKey = $_POST['apiKey'];

$url = 'https://fcm.googleapis.com/fcm/send';
 
$fields = array(
                'registration_ids'  => array( $registrationReceiverIDs ),
                'data'              => array( "message" => $message ,"sender" => $sender,"receiver" => $receiver,
                	"registrationSenderIDs"=>$registrationSenderIDs, "title" => $title,"chatRoom" => $chatRoom,"senderuid" => $senderuid)
                );
 
$headers = array( 
                    'Authorization: key=' . $apiKey,
                    'Content-Type: application/json'
                );
 

// Open connection
$ch = curl_init();
 
// Set the url, number of POST vars, POST data
curl_setopt( $ch, CURLOPT_URL, $url );
 
curl_setopt( $ch, CURLOPT_POST, true );
curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
curl_setopt( $ch, CURLOPT_SSL_VERIFYHOST, 0);
curl_setopt( $ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt( $ch, CURLOPT_IPRESOLVE, CURL_IPRESOLVE_V4); 
 
curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );
 
// Execute post
$result = curl_exec($ch);
 
// Close connection
curl_close($ch);
 
echo $result;
 
?>