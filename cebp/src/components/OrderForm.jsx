import { Box, TextField, Button, Typography } from "@mui/material";
import { useEffect, useState } from "react";

export default function OrderForm({heading, onSubmit}) {
    const [company, setCompany] = useState('');
    const [price, setPrice] = useState(0);
    const [quantity, setQuantity] = useState(0);

    useEffect(() => {
        console.log("Company:", company);
        console.log("Price:", price);
        console.log("Quantity:", quantity);
    }, [company, price, quantity]);

    return (
        <>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        gap: 4,
                        padding: 12,
                        border: '1px solid #aaa',
                        borderRadius: 4,
                        boxShadow: '2px 2px 8px rgba(0,0,0,0.2)',
                        width: '100%',
                        maxWidth: '500px',
                        alignItems: 'center',
                    }}
                >
                    {/* Heading Text */}
                    <Typography
                        variant="h4"
                        sx={{
                            marginBottom: 3,
                            color: '#333',
                            fontWeight: 'bold',
                        }}
                    >
                        {heading}
                    </Typography>

                    <TextField
                        id="company"
                        label="Company Name"
                        variant="outlined"
                        value={company}
                        onChange={(e) => setCompany(e.target.value)}
                        sx={{
                            width: '100%',
                            fontSize: '1.1rem',
                            '& .MuiOutlinedInput-root': {
                                borderColor: '#4CAF50',
                            },
                        }}
                    />

                    <TextField
                        id="price"
                        label="Price"
                        type="number"
                        variant="outlined"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        sx={{
                            width: '100%',
                            fontSize: '1.1rem',
                            '& .MuiOutlinedInput-root': {
                                borderColor: '#4CAF50',
                            },
                        }}
                    />

                    <TextField
                        id="quantity"
                        label="Quantity"
                        type="number"
                        variant="outlined"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                        sx={{
                            width: '100%',
                            fontSize: '1.1rem',
                            '& .MuiOutlinedInput-root': {
                                borderColor: '#4CAF50',
                            },
                        }}
                    />

                    <Button
                        variant="contained"
                        color="success"
                        sx={{
                            marginTop: 2,
                            width: '100%',
                            padding: '12px 20px',
                            fontSize: '1rem',
                            borderRadius: 3,
                            boxShadow: '0px 4px 6px rgba(0,0,0,0.3)',
                            backgroundColor: '#333',
                            '&:hover': {
                                backgroundColor: '#535353',
                            },
                        }}
                        onClick={() => onSubmit({ company, price, quantity })}
                    >
                        Submit
                    </Button>
                </Box>
            </Box>
        </>
    );
}
