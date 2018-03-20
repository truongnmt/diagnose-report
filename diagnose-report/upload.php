<?php
	require_once 'dbconnect.php';

	$upload_path = 'uploads/';
	$server_ip = gethostbyname(gethostname());

  // $upload_url = $server_ip.'/upload-image/'.$upload_path;
  //For android - localhost
	$upload_url = 'http://10.0.2.2:8888/diagnose-report/'.$upload_path;

	$response = array();

	if($_SERVER['REQUEST_METHOD']=='POST'){
		if(isset($_POST['name']) and count($_FILES['images']['name'])>0) {
			$name = $_POST['name'];

      for($i = 0; $i < count($_FILES['images']['name']); $i ++) {
        $fileinfo = pathinfo($_FILES['images']['name'][$i]);
  			$extension = $fileinfo['extension'];
  			$file_url = $upload_url . getFileName($conn) . '.' . $extension;
        $file_path = $upload_path . getFileName($conn) . '.'. $extension;

        try {
          move_uploaded_file($_FILES['images']['tmp_name'][$i],$file_path);
          $sql = "INSERT INTO `android`.`images` (`id`, `name`, `url`) VALUES (NULL, '$name', '$file_url');";
          mysqli_query($conn,$sql);
        } catch(Exception $e){
          $response['result']=false;
          $response['message']=$e->getMessage();

          echo json_encode($response);
          mysqli_close($conn);
        }
      }
			$response['result']=true;
      $response['message']="Upload successfully.";

      echo json_encode($response);
      mysqli_close($conn);
		} else {
      $response['result']=false;
			$response['message']='Please choose a file';

      echo json_encode($response);
      mysqli_close($conn);
		}
	}

	function getFileName($conn){
		$sql = "SELECT max(id) as id FROM images";
		$result = mysqli_fetch_array(mysqli_query($conn,$sql));

		if($result['id']==null)
			return 1;
		else
			return ++$result['id'];
	}
