import { Box} from '@mui/material';
import GlobalStyles from "@mui/styled-engine";
// import { styled } from '@mui/system';
import Slide from './Slide.jsx';
// import Grid from '@mui/material/Grid';


const Component = GlobalStyles(Box)`
    display: flex;
`

const LeftComponent = GlobalStyles(Box)(({ theme}) => ({
    width: '83%',
    [theme.breakpoints.down('md')]: {
        width: '100%'
    }
}))

const RightComponent = GlobalStyles(Box)(({ theme}) => ({
    marginTop: 10,
    // marginBottom: '10px',
    background: '#FFFFFF',
    width: '17%',
    marginLeft: 10,
    // height: '90px',
    padding: 5,
    textAlign: 'center',
    [theme.breakpoints.down('md')]: {
        display: 'none'
    },
}));

const MidSlide = ({ products }) => {
    const adURL = 'https://rukminim1.flixcart.com/flap/464/708/image/633789f7def60050.jpg?q=70';

    return (
        <Component>
            <LeftComponent>
                <Slide 
                    data={products} 
                    title='Deals of the Day'
                    timer={true} 
                    multi={true} 
                />
            </LeftComponent>
            <RightComponent>
                <img src={adURL} alt="Advertisement for latest deals on Flixcart" style={{width: 217}}/>
            </RightComponent>
        </Component>
    )
}

export default MidSlide;