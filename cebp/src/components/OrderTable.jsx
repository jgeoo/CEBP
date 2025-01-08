import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Typography } from "@mui/material";

export default function OrderTable({ orders, heading }) {
    return (
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
                {heading}
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
                        {orders.map((order) => (
                            <TableRow
                                key={order.id}
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
                                <TableCell>{order.company}</TableCell>
                                <TableCell>{order.quantity}</TableCell>
                                <TableCell>{order.price}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Paper>
    );
}
