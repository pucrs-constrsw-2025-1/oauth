import express from 'express';

const app = express();
const port = process.env.PORT || 3000;

app.get('/health', (req, res) => {
  res.send('OAuth service is healthy');
});

app.listen(port, () => {
  console.log(`OAuth service is running on port ${port}`);
});
