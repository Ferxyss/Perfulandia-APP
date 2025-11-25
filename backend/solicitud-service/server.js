const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');

const app = express();
app.use(cors());
app.use(express.json());


const MONGO_URL = 'mongodb://127.0.0.1:27017/perfulandia';

mongoose.connect(MONGO_URL, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB conectado ->', MONGO_URL))
  .catch(err => console.error('Error conectando a MongoDB:', err));


const solicitudRouter = require('./rutas/solicitudRoutes');
app.use('/api/solicitudes', solicitudRouter);


app.get('/health', (req, res) => res.json({ ok: true }));


const PORT = process.env.PORT || 4001;
app.listen(PORT, () => console.log(`Solicitud service en http://localhost:${PORT}`));
