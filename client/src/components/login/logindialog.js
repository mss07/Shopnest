import { useState, useContext } from "react";

import { Box, Button, TextField, Typography } from "@mui/material";
import GlobalStyles from "@mui/styled-engine";

import Dialog from "@mui/material/Dialog";
import { authenticatesignup, authenticatelogin } from "../../service/api";
import { Datacontext } from "../../context/dataprovider";

const Loginbutton = GlobalStyles(Button)`
    text-transform: none;
    Background:#000000;
    color: #fff;
    height: 48px;
    border-radius: 2px;



`;
const Requesrotp = GlobalStyles(Button)`
    text-transform: none;
    Background:#fff;
    color: #2874f0;
    height: 48px;
    border-radius: 2px;
    box-shadow: 0 2px 4px 0 rgb(0 0 0/ 20%%);



`;
const Image = GlobalStyles(Box)`

  
  height: 83%;
  width: 28%;


   Background:#8A2BE2  url(https://static-assets-web.flixcart.com/www/linchpin/fk-cp-zion/img/login_img_c4a81e.png) center 85% no-repeat ;
   padding: 45px 35px;
   &>p, &>h5{
     color: #ffffffff;
   }

`;

const Component = GlobalStyles(Box)`
  height: 70vh;
  width: 90vh:



`;
const Wrapper = GlobalStyles(Box)`

   display:flex;
   flex-direction: column;
   padding: 25px 35px;
   flex:1;
   &>div, &> button, &>p{
   
      margin-top:20px;
   
   }




`;

const Text = GlobalStyles(Typography)`


font-size:12px;
color: #878787;




`;
const Createaccount = GlobalStyles(Typography)`

  font_size: 14px;
  text-align: centre:
  color: #2874f0;
  font-weight:600;
  cursor:pointer;





`;

const Error = GlobalStyles(Typography)`

    font-size: 16px;
    color : #ff6161;
    line-height: 0;
    margin-top: 10px:
    font-weight: 600;



`
const accountinitialvalues = {
  login: {
    view: "login",
    heading: "login",
    subheading: "Get access to your orders, wishlist and recommendations",
  },
  signup: {
    view: "signup",
    heading: "looks like you are new here",
    subheading: "Sign up with your mobile number to get started",
  },
};

const signupinitialvalues = {
  firstname: "",
  lastname: "",
  username: "",
  email: "",
  password: "",
  phone: "",
};


const logininitialvalues = {

  username: '',
  password: ''




}

const Logindialog = ({ open, setopen }) => {
  const [account, toggleaccount] = useState(accountinitialvalues.login);
  const [signup, setsignup] = useState(signupinitialvalues);
  const [login,setlogin] = useState(logininitialvalues);
  const [error, seterror] =  useState(false);

  const {setaccount} = useContext(Datacontext);

  const handleclose = () => {
    setopen(false);
    toggleaccount(accountinitialvalues.login);
    seterror(false);
  };

  const togglesignup = () => {
    toggleaccount(accountinitialvalues.signup);
  };
  const onInputchange = (e) => {
    setsignup({ ...signup, [e.target.name]: e.target.value });
  };

  const signupuser = async () => {
    let response=  await authenticatesignup(signup);
    if(!response) return;
    handleclose();
    setaccount(signup.firstname);

  };

  const onvaluechange = (e)=>{

    setlogin({...login, [e.target.name]: e.target.value});



  };

  const loginuser = async()=>{
    
    let response = await authenticatelogin(login);
    console.log(response);
    if(response.status===200){

      handleclose();
      setaccount(response.data.data.firstname);
    }

    else{

      seterror(true);
    }




  };

  return (
    <Dialog
      open={open}
      onClose={handleclose}
      PaperProps={{ sx: { maxWidth: "unset" } }}
    >
      <Component>
        <Box style={{ display: "flex", height: "100%" }}>
          <Image>
            <Typography variant="h5">{account.heading}</Typography>
            <Typography style={{ marginTop: 20 }}>
              {account.subheading}
            </Typography>
          </Image>
          {account.view === "login" ? (
            <Wrapper>
              <TextField
                variant="standard"
                onChange={(e)=> onvaluechange(e)}
                name="username"
                label="Enter username"
              />
              { error && <Error>please enter valid username or password</Error>}
              <TextField variant="standard"   onChange={(e)=> onvaluechange(e)}
                name="password"label="password " />
              <Text>
                By continuing, you agree to flipkart terms of use and privacy
                policy
              </Text>

              <Loginbutton onClick={()=> loginuser()}>Login</Loginbutton>
              <Typography style={{ textAlign: "center" }}>OR</Typography>
              <Requesrotp>Request OTP</Requesrotp>
              <Createaccount
                style={{ textAlign: "center" }}
                onClick={() => togglesignup()}
              >
                New to flipkart? create account
              </Createaccount>
            </Wrapper>
          ) : (
            <Wrapper>
              <TextField
                variant="standard"
                onChange={(e) => onInputchange(e)}
                name="firstname"
                label="Enter first name "
              />
              <TextField
                variant="standard"
                onChange={(e) => onInputchange(e)}
                name="lastname"
                label="Enter last name "
              />
              <TextField
                variant="standard"
                onChange={(e) => onInputchange(e)}
                name="username"
                label="Enter username "
              />
              <TextField
                variant="standard"
                onChange={(e) => onInputchange(e)}
                name="email"
                label="Enter Email "
              />
              <TextField
                variant="standard"
                onChange={(e) => onInputchange(e)}
                name="password"
                label="Enter password"
              />
              <TextField
                variant="standard"
                onChange={(e) => onInputchange(e)}
                name="phone"
                label="Enter phone"
              />

              <Loginbutton onClick={signupuser}>Continue</Loginbutton>
            </Wrapper>
          )}
        </Box>
      </Component>
    </Dialog>
  );
};

export default Logindialog;
