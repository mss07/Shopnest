import Carousel from "react-multi-carousel";
import 'react-multi-carousel/lib/styles.css';
import  {Box,Typography,Button,Divider} from '@mui/material';
import GlobalStyles from "@mui/styled-engine";
import { Link } from 'react-router-dom';
import Countdown from 'react-countdown';
const responsive = {
    
    
    desktop: {
      breakpoint: { max: 3000, min: 1024 },
      items: 5
    },
    tablet: {
      breakpoint: { max: 1024, min: 464 },
      items: 2
    },
    mobile: {
      breakpoint: { max: 464, min: 0 },
      items: 1
    }
  };

const Component = GlobalStyles(Box)`
  margin-top: 10px;
  Background: #ffffff




`;

const Deal = GlobalStyles(Box)`

padding : 15px 20px;
 display: flex;    


`;

const DealText = GlobalStyles(Typography)`
    font-size: 22px;
    font-weight: 600;
    line-height: 32px;
    margin-right: 25px;
`;

const Timer = GlobalStyles(Box)`
    color: #7f7f7f;
    margin-left: 10px;
    display: flex;
    align-items: center;
`;

const ViewAllButton = GlobalStyles(Button)`
    margin-left: auto;
    background-color:  #8A2BE2;
    border-radius: 2px;
    font-size: 13px;
    font-weight: 600;
`;

const Image = GlobalStyles('img')({
    width: 'auto',
    height: 150
})

const Text = GlobalStyles(Typography)`
    font-size: 14px;
    margin-top: 5px;
`

const RenderTimer = GlobalStyles(Box)(({ theme }) => ({
    [theme.breakpoints.down('sm')]: {
        display: 'none'
    }
}));


const MultiSlide = ({ data, timer, title }) => {
    const timerURL = 'https://static-assets-web.flixcart.com/www/linchpin/fk-cp-zion/img/timer_a73398.svg';

    const renderer = ({ hours, minutes, seconds }) => {
        return <RenderTimer variant="span">{hours} : {minutes} : {seconds}  Left</RenderTimer>;
    };
    
    return (
        <Component>
            <Deal>
                <DealText>{title}</DealText>
                {
                    timer && <Timer>
                                <img src={timerURL} style={{ width: 24 }} alt='time clock' />
                                <Countdown date={Date.now() + 5.04e+7} renderer={renderer} />
                        </Timer>
                }
                <ViewAllButton variant="contained" color="primary">View All</ViewAllButton>
            </Deal>
            <Divider />
            <Carousel
                swipeable={false}
                draggable={false}
                responsive={responsive}
                centerMode={true}
                infinite={true}
                autoPlay={true}
                autoPlaySpeed={10000}
                keyBoardControl={true}
                showDots={false}
                containerClass="carousel-container"
                // removeArrowOnDeviceType={["tablet", "mobile"]}
                dotListClass="custom-dot-list-style"
                itemClass="carousel-item-padding-40-px"
            >
                {
                    data.map((temp) => (
                        <Link to={`product/${temp.id}`} style={{textDecoration: 'none'}} key={temp.id}>
                            <Box textAlign="center" style={{ padding: '25px 15px' }}>
                                <Image src={temp.url} />
                                <Text style={{ fontWeight: 600, color: '#212121' }}>{temp.title.shortTitle}</Text>
                                <Text style={{ color: 'green' }}>{temp.discount}</Text>
                                <Text style={{ color: '#212121', opacity: '.6' }}>{temp.tagline}</Text>
                            </Box>
                        </Link>
                    ))
                }
            </Carousel>
        </Component>
    )
}

const Slide = (props) => {
    return (
        <>
            {
                props.multi === true && <MultiSlide {...props} />
            }
        </>
    )
}


export default Slide;