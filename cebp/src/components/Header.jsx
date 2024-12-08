import {AppBar, Box, IconButton, Toolbar, Tooltip} from "@mui/material";
import InventoryOutlinedIcon from '@mui/icons-material/InventoryOutlined';
import SellOutlinedIcon from '@mui/icons-material/SellOutlined';
import ShoppingCartOutlinedIcon from '@mui/icons-material/ShoppingCartOutlined';
import Logo from "../assets/logo-no-background.svg";
import {Link} from "react-router";

export default function Header() {
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
                <Link to='/'>
                    <img
                        src={Logo}
                        alt="Stock Exchange"
                        style={{
                            maxWidth: '40%',
                        }}
                    />
                </Link>

                <Box sx = {{display:'flex', justifyContent:'space-between', width:'10%'}}>

                    <Link to='/sell-orders'>
                        <Tooltip title="Sell Orders" arrow>
                            <IconButton sx={{color: '#333'}}>
                                <SellOutlinedIcon/>
                            </IconButton>
                        </Tooltip>
                    </Link>

                    <Link to ='/buy-orders'>
                        <Tooltip title="Buy Orders" arrow>
                            <IconButton sx ={{color:'#333'}}>
                                <ShoppingCartOutlinedIcon />
                            </IconButton>
                        </Tooltip>
                    </Link>

                    <Link to ='/transactions'>
                        <Tooltip title="Transactions" arrow>
                            <IconButton sx ={{color:'#333'}}>
                                <InventoryOutlinedIcon />
                            </IconButton>
                        </Tooltip>
                    </Link>

                </Box>
            </Toolbar>
        </AppBar>
        </Box>
    );
}