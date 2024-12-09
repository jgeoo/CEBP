import { useState, useEffect } from "react";
import { Box, Button, Typography, Snackbar, Alert, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@mui/material";
import axios from "axios";
import OrderForm from "../components/OrderForm.jsx";

export default function SellOrders() {
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState(null);
    const [severity, setSeverity] = useState("success");
    const [sellOrders, setSellOrders] = useState([]);

    const toggleForm = () => {
        setShowForm(prev => !prev);
    };

    const handleSellSubmit = async (data) => {
        try {
            console.log("Submitting Sell Order:", data);
            const response = await axios.post('http://localhost:8080/api/stock-exchange/sell', {
                ...data,
                is_buy_order: false,
            });
            console.log("Sell Order Response:", response.data);

            setMessage("Successfully placed sell order!");
            setSeverity("success");

            fetchSellOrders();
        } catch (error) {
            console.error("Error submitting Sell Order:", error);
            setMessage("Failed to place sell order. Please try again.");
            setSeverity("error");
        }
    };

    const handleCloseSnackbar = () => {
        setMessage(null);
    };

    const fetchSellOrders = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/stock-exchange/sell-orders');
            setSellOrders(response.data);
        } catch (error) {
            console.error("Error fetching sell orders:", error);
            setMessage("Failed to load sell orders.");
            setSeverity("error");
        }
    };

    useEffect(() => {
        fetchSellOrders();
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
            <Box
                sx={{
                    position: 'absolute',
                    top: '150px',
                    zIndex: 100,
                }}
            >
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={toggleForm}
                    sx={{
                        padding: '10px 20px',
                        fontSize: '1rem',
                        borderRadius: 3,
                        backgroundColor: '#333',
                    }}
                >
                    {showForm ? "Close Sell Order Form" : "Open Sell Order Form"}
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
                        heading="Enter Your Sell Order Details"
                        onSubmit={handleSellSubmit}
                    />
                )}

                <Typography variant="h3" sx={{ margin: '20px 0' }}>Existing Sell Orders</Typography>

                <TableContainer
                    component={Paper}
                    sx={{
                        maxHeight: '300px', // Control height
                        width: '60%', // Restrict the width for responsiveness
                        margin: '20px auto', // Center the table horizontally
                        padding: '10px',
                        boxShadow: 3, // Add some depth
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
                            {sellOrders.map(order => (
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
