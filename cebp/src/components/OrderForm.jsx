import { Box, TextField, Button, Typography } from "@mui/material";
import { useState } from "react";

export default function OrderForm({ heading, onSubmit }) {
    const [company, setCompany] = useState('');
    const [price, setPrice] = useState(0);
    const [quantity, setQuantity] = useState(0);

    const isFormValid = company && price > 0 && quantity > 0;

    return (
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                padding: '0px 20px 20px 0',
            }}
        >
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: 3,
                    padding: 4,
                    border: '1px solid #ddd',
                    borderRadius: 4,
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
                    width: '100%',
                    maxWidth: 500,
                    backgroundColor: '#fff',
                    alignItems: 'center',
                }}
            >
                <Typography
                    variant="h5"
                    sx={{
                        marginBottom: 2,
                        color: '#333',
                        fontWeight: '600',
                        fontSize: '1.25rem',
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
                        fontSize: '1rem',
                        '& .MuiOutlinedInput-root': {
                            borderColor: '#4CAF50',
                        },
                    }}
                    required
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
                        fontSize: '1rem',
                        '& .MuiOutlinedInput-root': {
                            borderColor: '#4CAF50',
                        },
                    }}
                    required
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
                        fontSize: '1rem',
                        '& .MuiOutlinedInput-root': {
                            borderColor: '#4CAF50',
                        },
                    }}
                    required
                />

                <Box sx={{ width: '100%', display: 'flex', justifyContent: 'space-between', gap: 1 }}>
                    <Button
                        variant="outlined"
                        color="error"
                        sx={{
                            width: '48%',
                            padding: '10px 15px',
                            fontSize: '1rem',
                            borderRadius: 3,
                            backgroundColor: '#fff',
                            borderColor: '#f44336',
                            '&:hover': {
                                backgroundColor: '#f44336',
                                color: '#fff',
                            },
                        }}
                        onClick={() => {
                            setCompany('');
                            setPrice(0);
                            setQuantity(0);
                        }}
                    >
                        Clear
                    </Button>
                    <Button
                        variant="contained"
                        color="success"
                        sx={{
                            width: '48%',
                            padding: '12px 20px',
                            fontSize: '1rem',
                            borderRadius: 3,
                            backgroundColor: '#333',
                            boxShadow: '0 4px 6px rgba(0,0,0,0.2)',
                            '&:hover': {
                                backgroundColor: '#535353',
                            },
                        }}
                        onClick={() => onSubmit({ company, price, quantity })}
                        disabled={!isFormValid}
                    >
                        Submit
                    </Button>
                </Box>
            </Box>
        </Box>
    );
}
