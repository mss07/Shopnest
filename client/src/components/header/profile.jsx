

import { Box , Typography, Menu, MenuItem} from "@mui/material"
import { useState } from "react";
import PowerSettingsNewIcon from '@mui/icons-material/PowerSettingsNew';

import GlobalStyles from "@mui/styled-engine" ;

const Component = GlobalStyles(Menu)`

  marginTop: 8px;



`

const Logout = GlobalStyles(Typography)`


font-size: 14px;
margin-left:20px;




`



const Profile = ({account,setaccount})=> {
    const [open, setOpen]=  useState(false);
    const handleClick = (event)=>{

        setOpen(event.currentTarget);


    }


    const handleClose= ()=>{
      setOpen(false);



    }

    const logoutuser=()=>{
         setaccount('');






    }
    return(
         <>
         
         <Box onClick= {handleClick}><Typography style = {{marginTop : 2, cursor: 'pointer'}}>{account}</Typography></Box>

         <Component
       
        anchorEl={open}
        open={Boolean(open)}
        onClose={handleClose}
        
      >
        <MenuItem onClick={()=>{handleClose(); logoutuser();}}>
        <PowerSettingsNewIcon color="primary" fontsize = "small" />
        
        <Logout>Logout</Logout>
        </MenuItem>
      </Component>
         </>
        


    )





}

export default Profile;