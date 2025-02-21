import { createContext, useState } from "react";

export const Datacontext =  createContext(null);


const DataProvider = ({children})=>{

    const [account , setaccount] = useState('');
     return (

        <Datacontext.Provider value={{

         account,
         setaccount




        }}>
            {children}


        </Datacontext.Provider>


     )




}

export default DataProvider;