import { Box, Typography, CircularProgress } from "@mui/material";
import { useEffect, useState } from "react";
import axios from "axios";

export default function Transactions() {
    const [loading, setLoading] = useState(false);
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        const fetchTransactions = async () => {
            try {
                setLoading(true);
                const response = await axios.get('http://localhost:8080/api/stock-exchange/transactions');
                setTransactions(response.data);
            } catch (error) {
                console.error("Error fetching transactions:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchTransactions();
    }, []);

    if (loading) {
        return (
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '200vh',
                }}
            >
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx = {{paddingTop: '200px', height: '200vh'}}>
            <Box
                sx={{
                    padding: '20px 40px',
                    backgroundColor: '#f8f8f8',
                    borderRadius: '8px',
                    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
                    maxWidth: '800px',
                    margin: '20px auto',
                }}
            >
                <Typography
                    variant="h5"
                    sx={{
                        fontWeight: 'bold',
                        color: '#333',
                        marginBottom: '20px',
                    }}
                >
                    Transaction History
                </Typography>

                {/* Transaction List */}
                {transactions.length > 0 ? (
                    transactions.map((transaction) => (
                        <Box
                            key={transaction.id}
                            sx={{
                                padding: '10px 15px',
                                borderBottom: '1px solid #ddd',
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                                gap: '10px',
                            }}
                        >
                            <Typography
                                sx={{
                                    color: '#555',
                                    fontSize: '1rem',
                                }}
                            >
                                {transaction.company}
                            </Typography>
                            <Typography
                                sx={{
                                    color: '#555',
                                    fontSize: '1rem',
                                    fontWeight: 'bold',
                                }}
                            >
                                {transaction.quantity} shares at ${transaction.price}
                            </Typography>
                        </Box>
                    ))
                ) : (
                    <Typography
                        sx={{
                            textAlign: 'center',
                            color: '#555',
                            fontSize: '1rem',
                            margin: '20px 0',
                        }}
                    >
                        No transactions available.
                    </Typography>
                )}
            </Box>
        </Box>
    );
}
