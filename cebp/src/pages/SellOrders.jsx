import { useState, useEffect } from "react";
import { Box, Button, Snackbar, Alert, CircularProgress, Typography } from "@mui/material";
import axios from "axios";
import OrderForm from "../components/OrderForm.jsx";
import OrderTable from "../components/OrderTable.jsx";

export default function SellOrders() {
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState(null);
    const [severity, setSeverity] = useState("success");
    const [buyOrders, setBuyOrders] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    const toggleForm = () => {
        setShowForm(prev => !prev);
    };

    const handleSellSubmit = async (data) => {
        try {
            console.log("Submitting Sell Order:", data);
            await axios.post('http://localhost:8080/api/stock-exchange/sell', {
                ...data,
                is_buy_order: false,
            });
            setMessage("Successfully placed sell order!");
            setSeverity("success");
            fetchBuyOrders();
        } catch (error) {
            console.error("Error submitting Sell Order:", error);
            setMessage("Failed to place sell order. Please try again.");
            setSeverity("error");
        }
    };

    const handleCloseSnackbar = () => {
        setMessage(null);
    };

    const fetchBuyOrders = async () => {
        setIsLoading(true);
        try {
            const response = await axios.get('http://localhost:8080/api/stock-exchange/buy-orders');
            setBuyOrders(response.data);
        } catch (error) {
            console.error("Error fetching buy orders:", error);
            setMessage("Failed to load buy orders.");
            setSeverity("error");
        } finally {
            setIsLoading(false);
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
                minHeight: '150vh',
                flexDirection: 'column',
                padding: '20px',
                '@media (max-width: 600px)': {
                    padding: '10px',
                },
                marginTop: 10
            }}
        >
            {/* Title Section */}
            <Box sx={{ textAlign: 'center', marginBottom: 3 }}>
                <Typography
                    variant="h4"
                    sx={{
                        fontWeight: 'bold',
                        color: '#333',
                        marginBottom: 1,
                    }}
                >
                    Place a Sell Order for Available Buy Orders
                </Typography>
                <Typography
                    variant="body1"
                    sx={{
                        color: '#555',
                        lineHeight: 1.6,
                        maxWidth: 600,
                        margin: '0 auto',
                    }}
                >
                    The buy orders listed below are requests placed by other users who wish to buy items. You can place a sell order to offer items to these buyers through the form provided below.
                </Typography>
            </Box>

            {/* Loading or Order Table */}
            {isLoading ? (
                <CircularProgress />
            ) : (
                <OrderTable orders={buyOrders} heading="Existing Buy Orders" />
            )}

            {/* Snackbar for Feedback */}
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

            {/* Sell Order Form Toggle */}
            <Box sx={{ position: 'relative', margin: '50px 0', zIndex: 100 }}>
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={toggleForm}
                    sx={{
                        padding: '12px 24px',
                        fontSize: '1.1rem',
                        borderRadius: 3,
                        backgroundColor: '#333',
                        '&:hover': {
                            backgroundColor: '#444',
                        },
                    }}
                >
                    {showForm ? "Close Sell Order Form" : "Open Sell Order Form"}
                </Button>
            </Box>

            {/* Sell Order Form */}
            {showForm && (
                <Box sx={{ width: '100%', maxWidth: 600 }}>
                    <OrderForm
                        heading="Enter Your Sell Order Details"
                        onSubmit={handleSellSubmit}
                    />
                </Box>
            )}
        </Box>
    );
}
