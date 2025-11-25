const Solicitud = require('../modelos/solicitud');


exports.createSolicitud = async (req, res) => {
  try {
    const { emailUsuario, asunto, mensaje } = req.body;
    if (!emailUsuario || !asunto || !mensaje) {
      return res.status(400).json({ error: 'Faltan campos obligatorios' });
    }
    const nueva = new Solicitud({ emailUsuario, asunto, mensaje, synced: true });
    await nueva.save();

    const obj = nueva.toObject({ getters: true });
    obj._id = nueva._id.toString();

    return res.status(201).json(obj);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: 'Error al crear solicitud' });
  }
};

exports.listSolicitudes = async (req, res) => {
  try {
    const { email } = req.query;
    console.log('HTTP GET /api/solicitudes query=', req.query);
    const filtro = email ? { emailUsuario: email } : {};
    const items = await Solicitud.find(filtro).sort({ createdAt: -1 });

 
    const plain = items.map(item => {
      const o = item.toObject({ getters: true });
      o._id = item._id.toString();
      return o;
    });

    console.log(`Returning ${plain.length} solicitudes (filter=${JSON.stringify(filtro)})`);
    return res.json(plain);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ error: 'Error al obtener solicitudes' });
  }
};

exports.deleteSolicitud = async (req, res) => {
  const id = req.params.id;
  try {
    const removed = await Solicitud.findByIdAndDelete(id);
    if (!removed) {
      return res.status(404).json({ error: 'Solicitud no encontrada' });
    }
    return res.status(204).send();
  } catch (err) {
    console.error('Error deleting solicitud:', err);
    return res.status(500).json({ error: 'Error interno al borrar solicitud' });
  }
};
