import OrderForm from "../components/OrderForm.jsx";
import { useState } from "react";
import { Box, Button, Typography, Snackbar, Alert } from "@mui/material";
import axios from "axios";

export default function BuyOrders() {
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState(null); // Holds success or error message
    const [severity, setSeverity] = useState("success"); // Controls snackbar color (success/error)

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

            // Show success message
            setMessage("Successfully placed buy order!");
            setSeverity("success");
        } catch (error) {
            console.error("Error submitting Buy Order:", error);
            // Show error message
            setMessage("Failed to place buy order. Please try again.");
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
                    marginTop: '60px',
                    width: '100%',
                }}
            >
                {showForm && (
                    <OrderForm
                        heading="Enter Your Buy Order Details"
                        onSubmit={handleBuySubmit}
                    />
                )}

                <Typography>Buy Orders Available:</Typography>
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
