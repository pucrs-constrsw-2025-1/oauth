// index.ts
import app from './app'; // Importar o app configurado do app.ts

const port = process.env.PORT || process.env.OAUTH_INTERNAL_API_PORT;

// Opcional: Mantenha a rota /health se desejar, ela será adicionada ao app principal
app.get('/health', (req, res) => {
  res.send('OAuth service is healthy');
});

app.listen(port, () => {
  console.log(`OAuth service is running on port ${port}`);
});
