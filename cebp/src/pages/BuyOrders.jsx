import { useState, useEffect } from "react";
import { Box, Button, Typography, Snackbar, Alert, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@mui/material";
import axios from "axios";
import OrderForm from "../components/OrderForm.jsx";

export default function BuyOrders() {
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState(null);
    const [severity, setSeverity] = useState("success");
    const [buyOrders, setBuyOrders] = useState([]);

    const toggleForm = () => {
        setShowForm(prev => !prev);
    };

    const handleBuySubmit = async (data) => {
        try {
            console.log("Submitting Buy Order:", data);
            const response = await axios.post('http://localhost:8080/api/stock-exchange/buy', {
                ...data,
                is_buy_order: true,
            });
            console.log("Buy Order Response:", response.data);

            setMessage("Successfully placed buy order!");
            setSeverity("success");

            fetchBuyOrders();
        } catch (error) {
            console.error("Error submitting Buy Order:", error);
            setMessage("Failed to place buy order. Please try again.");
            setSeverity("error");
        }
    };

    const handleCloseSnackbar = () => {
        setMessage(null);
    };

    const fetchBuyOrders = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/stock-exchange/buy-orders');
            setBuyOrders(response.data);
        } catch (error) {
            console.error("Error fetching buy orders:", error);
            setMessage("Failed to load buy orders.");
            setSeverity("error");
        }
    };

    useEffect(() => {
        fetchBuyOrders();
    }, []);

    return (
        <Box
            sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                minHeight: '100vh',
                flexDirection: 'column',
                padding: '20px',
            }}
        >
            {/* Button to toggle buy order form */}
            <Box
                sx={{
                    position: 'absolute',
                    top: '150px',
                    zIndex: 100,
                }}
            >
                <Button
                    variant="contained"
                    color="primary"
                    onClick={toggleForm}
                    sx={{
                        padding: '10px 20px',
                        fontSize: '1rem',
                        borderRadius: 3,
                        backgroundColor: '#333',
                    }}
                >
                    {showForm ? "Close Buy Order Form" : "Open Buy Order Form"}
                </Button>
            </Box>

            <Box
                sx={{
                    textAlign: 'center',
                    marginTop: '250px',
                    width: '100%',
                }}
            >
                {showForm && (
                    <OrderForm
                        heading="Enter Your Buy Order Details"
                        onSubmit={handleBuySubmit}
                    />
                )}

                <Typography variant="h4" sx={{ margin: '20px 0 20px 300px', textAlign: 'left'}}>Buy Order History:</Typography>

                {/* Table displaying available buy orders */}
                <TableContainer
                    component={Paper}
                    sx={{
                        maxHeight: '300px',
                        width: '60%',
                        margin: '20px auto',
                        padding: '10px',
                        boxShadow: 3,
                        borderRadius: 2,
                    }}
                >
                    <Table stickyHeader>
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ fontWeight: 'bold', textAlign: 'center' }}>Company</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', textAlign: 'center' }}>Quantity</TableCell>
                                <TableCell sx={{ fontWeight: 'bold', textAlign: 'center' }}>Price</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {buyOrders.map(order => (
                                <TableRow key={order.id}>
                                    <TableCell sx={{ textAlign: 'center' }}>{order.company}</TableCell>
                                    <TableCell sx={{ textAlign: 'center' }}>{order.quantity}</TableCell>
                                    <TableCell sx={{ textAlign: 'center' }}>{order.price}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>

            {/* Snackbar for displaying messages */}
            <Snackbar
                open={!!message}
                autoHideDuration={4000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert onClose={handleCloseSnackbar} severity={severity} sx={{ width: '100%' }}>
                    {message}
                </Alert>
            </Snackbar>
        </Box>
    );
}
