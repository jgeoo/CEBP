import { Box, Typography, TextField, Button } from "@mui/material";
import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom"; // Import useNavigate
import LoginImage from "../assets/home-image.jpg"; // Optional: Use a different image for sign up

export default function SignUp() {
    // State for form inputs
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState(""); // Add success message state

    // Use navigate to redirect after successful sign-up
    const navigate = useNavigate();

    // Handle form submission for sign-up
    const handleSignUp = async (e) => {
        e.preventDefault();

        // Clear any previous messages
        setErrorMessage("");
        setSuccessMessage("");

        try {
            // Sending a POST request with the username and password
            const response = await axios.post("http://localhost:8080/api/users", {
                username,
                password,
            });

            // Handle successful sign-up
            if (response.status === 200) {
                console.log("Sign-up successful:", response.data);

                // Display success message
                setSuccessMessage("Account created successfully! Redirecting to login...");

                // Redirect user to login page after successful sign-up
                setTimeout(() => {
                    navigate("/"); // Redirect to login page
                }, 2000); // Wait for 2 seconds before redirecting
            }
        } catch (error) {
            // Handle sign-up failure (e.g., user already exists or API error)
            setErrorMessage("Error signing up. Please try again.");
            console.error("Sign-up error:", error);
        }
    };
    const handleLogin = () => {
        navigate("/"); // Redirect to the Sign Up page
    };

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
                    Create a New Account
                </Typography>
                <Typography
                    variant="body1"
                    sx={{
                        color: "#555",
                        mb: 3,
                        lineHeight: 1.6,
                    }}
                >
                    Sign up to start trading stocks and managing your portfolio.
                </Typography>

                {/* Sign-Up Form */}
                <Box
                    component="form"
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        gap: 2,
                    }}
                    onSubmit={handleSignUp} // Bind form submission to the handleSignUp function
                >
                    <TextField
                        label="Username"
                        variant="outlined"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        sx={{
                            width: "100%",
                        }}
                    />
                    <TextField
                        label="Password"
                        type="password"
                        variant="outlined"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        sx={{
                            width: "100%",
                        }}
                    />
                    
                    <Button
                        type="submit"
                        variant="contained"
                        color="primary"
                        sx={{
                            marginTop: 2,
                            padding: "10px 20px",
                            backgroundColor: "#007bff",
                        }}
                    >
                        Sign Up
                    </Button>
                    <Button
                        variant="text"
                        color="primary"
                        onClick={handleLogin}
                        sx={{
                            marginTop: 2,
                            textTransform: "none",
                        }}
                    >
                        Already have an account?
                    </Button>
                </Box>

                {errorMessage && (
                    <Typography
                        variant="body2"
                        color="error"
                        sx={{
                            mt: 2,
                            textAlign: "center",
                        }}
                    >
                        {errorMessage}
                    </Typography>
                )}

                {successMessage && (
                    <Typography
                        variant="body2"
                        color="success"
                        sx={{
                            mt: 2,
                            textAlign: "center",
                        }}
                    >
                        {successMessage}
                    </Typography>
                )}
            </Box>

            <Box
                component="img"
                src={LoginImage} // Optionally use a different image for the sign-up page
                alt="Sign Up Illustration"
                sx={{
                    height: { xs: "300px", sm: "400px", md: "500px" },
                    borderRadius: "8px",
                    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)",
                }}
            />
        </Box>
    );
}
