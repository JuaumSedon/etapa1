const express = require('express');
const axios = require('axios');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = 3000;

// Configurações
app.use(cors());
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// Serve os arquivos HTML estáticos da pasta "public"
app.use(express.static(path.join(__dirname, 'public')));

// ROTA 1: GET / (Serve o index.html)
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// ROTA 2: POST /send-code (Envia o e-mail para o Java e redireciona)
app.post('/send-code', async (req, res) => {
    const { email } = req.body;
    try {
        // Chama a API em Java
        await axios.post('http://localhost:8081/users/auth/request-code', { email });
        // Redireciona para a tela de verificação passando o e-mail na URL
        res.redirect(`/verify?email=${encodeURIComponent(email)}`);
    } catch (error) {
        console.error("Erro ao solicitar código:", error.message);
        res.status(500).send("Erro ao processar a solicitação. Verifique se o backend Java está rodando.");
    }
});

// ROTA 3: GET /verify (Serve o verify.html)
app.get('/verify', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'verify.html'));
});

// ROTA 4: POST /verify-code (Valida o código no Java)
app.post('/verify-code', async (req, res) => {
    const { email, code } = req.body;
    try {
        const response = await axios.post('http://localhost:8081/users/auth/verify-code', { email, code });
        
        // Retorna sucesso para o HTML lidar com o redirecionamento
        res.json({ success: true, message: response.data });
    } catch (error) {
        console.error("Erro ao validar código:", error.message);
        res.status(400).json({ success: false, message: "Código inválido ou expirado." });
    }
});

// Inicia o servidor Node
app.listen(PORT, () => {
    console.log(`🚀 Frontend rodando em http://localhost:${PORT}`);
});