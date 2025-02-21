



//components
import Header from './components/header/header';
import Home from './components/home/Home';
import DataProvider from './context/dataprovider';
import { Routes,Route } from 'react-router-dom';
import { Box } from '@mui/material'; 
import DetailView from './components/details/DetailView';
import Cart from './components/Cart/Cart';

function App() {
  return (
    <DataProvider>
      
      <Header/>
        <Box style = {{marginTop: '65px'}}>
          <Routes>
          <Route path='/' element={<Home/>} />
          <Route path='/product/:id' element={<DetailView/>} />
          <Route path='/Cart' element={<Cart/>}/>
          
          </Routes>
       

        </Box>
        
        </DataProvider>
  );
}

export default App;
