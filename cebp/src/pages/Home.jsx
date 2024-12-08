import { Box, Typography } from "@mui/material";
import HomeImage from "../assets/home-image.jpg";
import ActionButtons from "../components/ActionButtons.jsx";

export default function Home() {
    return (
        <Box
            sx={{
                padding: { xs: "50px 20px", sm: "100px 30px", md: "100px 150px" },
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                flexWrap: "wrap",
                minHeight: "100vh",
            }}
        >
            <Box
                sx={{
                    maxWidth: "500px",
                    textAlign: { xs: "center", sm: "left" },
                    mb: { xs: "20px", sm: "0" },
                }}
            >
                <Typography
                    variant="h2"
                    sx={{
                        color: "#333",
                        fontWeight: "bold",
                        mb: 2,
                    }}
                >
                    Welcome to the Stock Exchange
                </Typography>
                <Typography
                    variant="body1"
                    sx={{
                        color: "#555",
                        mb: 3,
                        lineHeight: 1.6,
                    }}
                >
                    Trade stocks, manage your portfolio, and seize opportunities in real
                    time. Your journey into the world of trading starts here.
                </Typography>

                <ActionButtons />
            </Box>

            <Box
                component="img"
                src={HomeImage}
                alt="Stock Trading Illustration"
                sx={{
                    height: { xs: "300px", sm: "400px", md: "500px" },
                    borderRadius: "8px",
                    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)",
                }}
            />
        </Box>
    );
}
