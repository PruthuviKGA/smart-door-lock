const express = require('express');
const router = express.Router();
const VerificationLog = require('../models/VerificationLog');

// Get all verification logs (username and verifiedAt only)
router.get('/', async (req, res) => {
  try {
    const logs = await VerificationLog.find({}, 'username verifiedAt');
    const formattedLogs = logs.map(log => ({
      username: log.username,
      verifiedAt: log.verifiedAt,
    }));
    res.status(200).json(formattedLogs);
  } catch (error) {
    console.error('Server error:', error);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
