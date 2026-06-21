const express = require('express');
const axios = require('axios');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.post('/send-code', async (req, res) => {
    const { email } = req.body;
    try {
        await axios.post('http://localhost:8081/users/auth/request-code', { email });
        res.redirect(`/verify?email=${encodeURIComponent(email)}`);
    } catch (error) {
        console.error("Erro ao solicitar código:", error.message);
        res.status(500).send("Erro ao processar a solicitação.");
    }
});

app.get('/verify', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'verify.html'));
});

app.post('/verify-code', async (req, res) => {
    const { email, code } = req.body;
    try {
        const response = await axios.post('http://localhost:8081/users/auth/verify-code', { email, code });
        res.json({ success: true, message: response.data });
    } catch (error) {
        console.error("Erro ao validar código:", error.message);
        res.status(400).json({ success: false, message: "Código inválido ou expirado." });
    }
});

app.get('/register', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'register.html'));
});

app.post('/register', async (req, res) => {
    const { name, role } = req.body;
    const token = req.headers['authorization']; 

    try {
        const response = await axios.post('http://localhost:8081/users/update-profile', 
            { name, role },
            { headers: { 'Authorization': token } } 
        );
        res.json({ success: true, data: response.data });
    } catch (error) {
        console.error("Erro ao atualizar perfil:", error.message);
        res.status(500).json({ success: false, message: "Erro ao atualizar." });
    }
});

app.get('/dashboard', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'dashboard.html'));
});

app.get('/api/protected', async (req, res) => {
    const token = req.headers['authorization'];
    const authHeader = token.startsWith('Bearer ') ? token : `Bearer ${token}`;

    const response = await axios.get('http://localhost:8081/users', {
    headers: { 'Authorization': authHeader }
    });

    try {
        const response = await axios.get('http://localhost:8081/users', {
            headers: { 'Authorization': authHeader }
        });
        res.json({ success: true, data: response.data });
    } catch (error) {
        console.error("Erro ao chamar o Java:", error.response?.status || error.message);
        res.status(403).json({ success: false, message: "Acesso Negado" });
    }
});

app.listen(PORT, () => {
    console.log(`🚀 Frontend rodando em http://localhost:${PORT}`);
});