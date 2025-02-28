const mongoose = require('mongoose');

const verificationLogSchema = new mongoose.Schema({
  username: {
    type: String,
    required: true,
  },
  fingerprintId: {
    type: String,
    required: true,
  },
  verifiedAt: {
    type: Date,
    default: Date.now,
  },
});

const VerificationLog = mongoose.model('VerificationLog', verificationLogSchema);

module.exports = VerificationLog;
