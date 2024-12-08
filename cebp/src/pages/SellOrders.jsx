import OrderForm from "../components/OrderForm.jsx";
import { useState } from "react";
import { Box, Button, Typography, Snackbar, Alert } from "@mui/material";
import axios from "axios";

export default function SellOrders() {
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState(null); // Holds success or error message
    const [severity, setSeverity] = useState("success"); // Controls snackbar color (success/error)

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

            // Show success message
            setMessage("Successfully placed sell order!");
            setSeverity("success");
        } catch (error) {
            console.error("Error submitting Sell Order:", error);
            // Show error message
            setMessage("Failed to place sell order. Please try again.");
            setSeverity("error");
        }
    };

    const handleCloseSnackbar = () => {
        setMessage(null);
    };

    return (
        <Box
            sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                height: '150vh',
                flexDirection: 'column',
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
                    marginTop: '60px',
                    width: '100%',
                }}
            >
                {showForm && (
                    <OrderForm
                        heading="Enter Your Sell Order Details"
                        onSubmit={handleSellSubmit}
                    />
                )}

                <Typography>Existing Sell Orders</Typography>
            </Box>

            {/* Snackbar for messages */}
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
