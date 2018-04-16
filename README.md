# Diagnose Report
This is a simple android application that help patients to manage their medical reports. 

We use machine learning in backend to analyse whether the patient's report images ([ECG](https://en.wikipedia.org/wiki/Electrocardiography)) is abnormal or not.

<div>
<img align="left" width="200" src="/screenshots/signup.png?raw=true">
<img align="left" width="200" src="/screenshots/dashboard.png?raw=true">
<img align="left" width="200" src="/screenshots/report_detail.png?raw=true">
<img align="left" width="200" src="/screenshots/create_report.png?raw=true">
<div>

## Setup
After you clone this repo to your desktop:
1. Copy `diagnose-report` folder to your htdocs. (Eg: for Ubuntu using XAMPP, see [here](https://stackoverflow.com/questions/17624936/xampp-ubuntu-cant-access-my-project-in-lampp-htdocs))
2. Import `diagnose_report.sql` file to your database
3. Open `dbconnect.php` and change variables according to your database. (`$servername`, `$username`, `$password`)
4. YOU ARE READYYYY!!!!

## Todo
- [ ] Add type of report
- [ ] Change the app tone
- [ ] Change to bottom navigation
- [ ] Add intro screen for new user

Pull requests are more than welcomed!

## License
This project is licensed under the terms of the **MIT** license.