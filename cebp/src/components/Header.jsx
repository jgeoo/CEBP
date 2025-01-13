import {AppBar, Box, IconButton, Toolbar, Tooltip} from "@mui/material";
import InventoryOutlinedIcon from '@mui/icons-material/InventoryOutlined';
import SellOutlinedIcon from '@mui/icons-material/SellOutlined';
import ShoppingCartOutlinedIcon from '@mui/icons-material/ShoppingCartOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import Logo from "../assets/logo-no-background.svg";
import {Link, useNavigate} from "react-router";
import {useEffect, useState} from "react";

export default function Header() {
    const navigate = useNavigate();
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    const handleLogout = () => {
        localStorage.clear(); // Clear local storage
        setIsLoggedIn(false); // Update state
        navigate("/"); // Navigate to the login page
    };

    useEffect(() => {
        const id = localStorage.getItem("id");
        setIsLoggedIn(!!id);
    }, []);

    return (
        <Box sx={{}}>
        <AppBar
        sx = {{
            backgroundColor: "#d4ddd4",
            padding: 1,
            boxShadow: 'none',
            paddingRight:'150px'
        }}
        >
            <Toolbar
                sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
            >
                <Link to='/home'>
                    <img
                        src={Logo}
                        alt="Stock Exchange"
                        style={{
                            maxWidth: '40%',
                        }}
                    />
                </Link>

                {isLoggedIn && (
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '10%' }}>
                        <Link to='/sell-orders'>
                            <Tooltip title="Sell Orders" arrow>
                                <IconButton sx={{ color: '#333' }}>
                                    <SellOutlinedIcon />
                                </IconButton>
                            </Tooltip>
                        </Link>

                        <Link to='/buy-orders'>
                            <Tooltip title="Buy Orders" arrow>
                                <IconButton sx={{ color: '#333' }}>
                                    <ShoppingCartOutlinedIcon />
                                </IconButton>
                            </Tooltip>
                        </Link>

                        <Link to='/transactions'>
                            <Tooltip title="Transactions" arrow>
                                <IconButton sx={{ color: '#333' }}>
                                    <InventoryOutlinedIcon />
                                </IconButton>
                            </Tooltip>
                        </Link>

                        <Tooltip title="Logout" arrow>
                            <IconButton sx={{ color: '#333' }} onClick={handleLogout}>
                                <LogoutOutlinedIcon />
                            </IconButton>
                        </Tooltip>
                    </Box>
                )}
            </Toolbar>
        </AppBar>
        </Box>
    );
}