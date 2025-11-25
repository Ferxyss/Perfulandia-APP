const mongoose = require('mongoose');


const SolicitudSchema = new mongoose.Schema({
  emailUsuario: { type: String, required: true },
  asunto: { type: String, required: true },
  mensaje: { type: String, required: true },
  createdAt: { type: Date, default: Date.now },
  synced: { type: Boolean, default: true }
}, { collection: 'solicitudes' });


module.exports = mongoose.model('Solicitud', SolicitudSchema, 'solicitudes');
