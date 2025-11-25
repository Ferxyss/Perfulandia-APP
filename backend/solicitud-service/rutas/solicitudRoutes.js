const express = require('express');
const router = express.Router();
const controller = require('../controllers/solicitudController');

router.post('/', controller.createSolicitud);
router.get('/', controller.listSolicitudes);
router.delete('/:id', controller.deleteSolicitud);

module.exports = router;
