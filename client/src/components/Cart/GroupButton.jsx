import React, { useState } from "react";
import GlobalStyles from "@mui/styled-engine" ;


import { ButtonGroup, Button } from "@mui/material";

const Component = GlobalStyles(ButtonGroup)`
    margin-top: 30px;
`;

const StyledButton = GlobalStyles(Button)`
    border-radius: 50%;
`;

const GroupedButton = () => {
    const [ counter, setCounter ] = useState(1);

    const handleIncrement = () => {
        setCounter(counter => counter + 1 );
    };

    const handleDecrement = () => {
        setCounter(counter => counter - 1 );
    };

    return (
        <Component>
            <StyledButton onClick={() => handleDecrement()} disabled={counter === 0}>-</StyledButton>
            <Button disabled>{counter}</Button>
            <StyledButton onClick={() => handleIncrement()}>+</StyledButton>
        </Component>
    );
}

export default GroupedButton;