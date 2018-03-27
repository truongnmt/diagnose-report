<?php
  require_once 'dbconnect.php';

  if($_SERVER['REQUEST_METHOD']=='GET'){
    if(isset($_GET['user_id'])) {
      $user_id = $_GET['user_id'];
      $sql = "SELECT * FROM images WHERE `user_id` = $user_id ORDER BY `id` DESC;";
      // die($sql);
      $result = mysqli_query($conn,$sql);
      $response = array();
      while($row = mysqli_fetch_array($result)){
      	$temp = array();
      	$temp['id']=$row['id'];
      	$temp['name']=$row['name'];
        $temp['url']=$row['url'];
        $temp['result']=$row['result'];
      	$temp['user_id']=$row['user_id'];
      	array_push($response, $temp);
      }
      echo json_encode($response);
    }
  }
