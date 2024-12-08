import React from "react";
import { Box, Button } from "@mui/material";
import { Link } from "react-router";

export default function ActionButtons() {
    const buttons = [
        { to: "/sell-orders", label: "Sell Orders" },
        { to: "/buy-orders", label: "Buy Orders" },
        { to: "/transactions", label: "Transactions" },
    ];

    return (
        <Box
            sx={{
                display: "flex",
                justifyContent: "space-between",
                gap: 2,
                flexWrap: "wrap",
                mt: 3,
            }}
        >
            {buttons.map((button) => (
                <Button
                    key={button.to}
                    component={Link}
                    to={button.to}
                    variant="contained"
                    sx={{
                        backgroundColor: "#333",
                        color: "#fff !important", // Ensures text remains white
                        padding: "10px 20px",
                        borderRadius: "8px",
                        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
                        transition: "background-color 0.3s ease, transform 0.2s ease",
                        "&:hover": {
                            backgroundColor: "#555",
                            transform: "translateY(-2px)",
                        },
                        "&:focus": {
                            outline: "2px solid #777",
                            outlineOffset: "2px",
                        },
                    }}
                >
                    {button.label}
                </Button>
            ))}
        </Box>
    );
}
