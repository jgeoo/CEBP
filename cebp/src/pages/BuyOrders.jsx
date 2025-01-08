import { useState, useEffect } from "react";
import { Box, Button, Snackbar, Alert, CircularProgress, Typography } from "@mui/material";
import axios from "axios";
import OrderForm from "../components/OrderForm.jsx";
import OrderTable from "../components/OrderTable.jsx";

export default function BuyOrders() {
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState(null);
    const [severity, setSeverity] = useState("success");
    const [sellOrders, setSellOrders] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    const toggleForm = () => {
        setShowForm(prev => !prev);
    };

    const handleBuySubmit = async (data) => {
        try {
            console.log("Submitting Buy Order:", data);
            await axios.post('http://localhost:8080/api/stock-exchange/buy', {
                ...data,
                is_buy_order: true,
            });
            setMessage("Successfully placed buy order!");
            setSeverity("success");
            fetchSellOrders();
        } catch (error) {
            console.error("Error submitting Buy Order:", error);
            setMessage("Failed to place buy order. Please try again.");
            setSeverity("error");
        }
    };

    const handleCloseSnackbar = () => {
        setMessage(null);
    };

    const fetchSellOrders = async () => {
        setIsLoading(true);
        try {
            const response = await axios.get('http://localhost:8080/api/stock-exchange/sell-orders');
            setSellOrders(response.data);
        } catch (error) {
            console.error("Error fetching sell orders:", error);
            setMessage("Failed to load sell orders.");
            setSeverity("error");
        } finally {
            setIsLoading(false);
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
                    Place a Buy Order for Available Sell Orders
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
                    The sell orders listed below are requests placed by other users who are offering items for sale. You can place a buy order to purchase these available sell orders through the form provided below.
                </Typography>
            </Box>

            {/* Loading or Order Table */}
            {isLoading ? (
                <CircularProgress />
            ) : (
                <OrderTable orders={sellOrders} heading="Existing Sell Orders" />
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

            {/* Buy Order Form Toggle */}
            <Box sx={{ position: 'relative', margin: '50px 0', zIndex: 100 }}>
                <Button
                    variant="contained"
                    color="primary"
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
                    {showForm ? "Close Buy Order Form" : "Open Buy Order Form"}
                </Button>
            </Box>

            {/* Buy Order Form */}
            {showForm && (
                <Box sx={{ width: '100%', maxWidth: 600 }}>
                    <OrderForm
                        heading="Enter Your Buy Order Details"
                        onSubmit={handleBuySubmit}
                    />
                </Box>
            )}
        </Box>
    );
}
