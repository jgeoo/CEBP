import { Box, Typography, CircularProgress, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@mui/material";
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
                    height: '200vh',
                    marginTop: '100px'
                }}
            >
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ paddingTop: '100px', height: '200vh' }}>
            <Paper
                sx={{
                    width: '70%',
                    margin: '20px auto',
                    padding: '10px',
                    boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.1)',
                    borderRadius: '12px',
                }}
            >
                <Typography
                    variant="h5"
                    sx={{
                        margin: '0 0 10px 0',
                        textAlign: 'center',
                        fontWeight: 500,
                        letterSpacing: '0.5px',
                    }}
                >
                    Transaction History
                </Typography>

                {/* Description of Accepted and Matching Transactions */}
                <Typography
                    sx={{
                        color: '#555',
                        fontSize: '1rem',
                        marginBottom: '20px',
                        textAlign: 'center',
                    }}
                >
                    These are the accepted and matching transactions that have been processed in the system.
                </Typography>

                <TableContainer
                    sx={{
                        maxHeight: '300px',
                        overflowY: 'auto',
                        '&::-webkit-scrollbar': {
                            width: '6px',
                        },
                        '&::-webkit-scrollbar-track': {
                            backgroundColor: '#f4f4f4',
                            borderRadius: '10px',
                        },
                        '&::-webkit-scrollbar-thumb': {
                            backgroundColor: '#cccccc',
                            borderRadius: '10px',
                        },
                    }}
                >
                    <Table stickyHeader>
                        <TableHead>
                            <TableRow
                                sx={{
                                    '& th': {
                                        background: 'linear-gradient(to right, #f9f9f9, #ececec)',
                                        fontWeight: 'bold',
                                        textAlign: 'center',
                                        borderBottom: '2px solid #e0e0e0',
                                        padding: '12px 16px',
                                        fontSize: '1rem',
                                        position: 'sticky',
                                        top: 0,
                                        zIndex: 2,
                                        boxShadow: '0px 2px 5px rgba(0, 0, 0, 0.05)',
                                    },
                                }}
                            >
                                <TableCell>Company</TableCell>
                                <TableCell>Quantity</TableCell>
                                <TableCell>Price</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {transactions.length > 0 ? (
                                transactions.map((transaction) => (
                                    <TableRow
                                        key={transaction.id}
                                        sx={{
                                            transition: 'background-color 0.2s ease, box-shadow 0.2s ease',
                                            '&:hover': {
                                                backgroundColor: '#f4f4f4',
                                                boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.05)',
                                            },
                                            '& td': {
                                                textAlign: 'center',
                                                padding: '12px 16px',
                                                borderBottom: '1px solid #e0e0e0',
                                                fontSize: '0.9rem',
                                            },
                                        }}
                                    >
                                        <TableCell>{transaction.company}</TableCell>
                                        <TableCell>{transaction.quantity}</TableCell>
                                        <TableCell>${transaction.price}</TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={3} sx={{ textAlign: 'center', color: '#555' }}>
                                        No transactions available.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Box>
    );
}
