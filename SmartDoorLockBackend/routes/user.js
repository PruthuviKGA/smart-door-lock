// routes/userRoutes.js
const express = require('express');
const router = express.Router();
const User = require('../models/user');
const VerificationLog = require('../models/VerificationLog');

// Create user API
router.post('/createuser', async (req, res) => {
  const { username, email, fingerprintId } = req.body;


  // Basic validation
  if (!username || !email) {
    return res.status(400).json({ message: 'Username and email are required' });
  }

  try {
    // Check if email already exists
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: 'Email already in use' });
    }

    // Create new user
    const newUser = new User({ username, email, fingerprintId});
    await newUser.save();

    res.status(201).json({ message: 'User created successfully', user: newUser });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Server error' });
  }
});

// Get all users API
router.get('/', async (req, res) => {
  try {
    const users = await User.find();
    res.status(200).json(users);  // Send back all users
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Server error' });
  }
});

// Verify user
router.put('/verify/:id', async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    user.isVerified = 1;  // Set isVerified to 1
    await user.save();
    res.status(200).json(user);  // Return updated user
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Server error' });
  }
});

// Block user (set osVerified to 2)
router.put('/block/:id', async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    user.isVerified = 2;  // Set osVerified to 2
    await user.save();
    res.status(200).json(user);  // Return updated user
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Server error' });
  }
});

// Verify fingerprint API and log verification
router.get('/verifyFingerprint/:fingerprintId', async (req, res) => {
  const { fingerprintId } = req.params;

  console.log('Received fingerprintId:', fingerprintId); // Debug log

  try {
    // Find the user with the given fingerprintId
    const user = await User.findOne({ fingerprintId });

    if (!user) {
      return res.status(404).json({ message: 'Fingerprint ID not found' });
    }

    // Check if the user is verified
    if (user.isVerified === 1) {
      const logEntry = new VerificationLog({
        username: user.username,
        fingerprintId: user.fingerprintId,
        verifiedAt: new Date(),
      });
      await logEntry.save();

      return res.status(200).json({ message: 'User is already verified', user });
    }

    // Update user to verified
    user.isVerified = 1;
    await user.save();

    // Log the verification
    const logEntry = new VerificationLog({
      username: user.username,
      fingerprintId: user.fingerprintId,
      verifiedAt: new Date(),
    });
    await logEntry.save();

    res.status(200).json({ message: 'User successfully verified', user });
  } catch (error) {
    console.error('Server error:', error);
    res.status(500).json({ message: 'Server error' });
  }
});



/// Get all user logs (including username, email, updatedAt, and filter by isVerified)
// router.get('/logs', async (req, res) => {
//   try {
//     // Fetch all users and filter by isVerified === 1
//     const users = await User.find({ isVerified: 1 });

//     if (!users || users.length === 0) {
//       return res.status(404).json({ message: 'No verified users found' });
//     }

//     // Map the result to include the desired fields
//     const userLogs = users.map(user => ({
//       username: user.username,
//       email: user.email,
//       updatedAt: user.updatedAt,
//     }));

//     res.status(200).json(userLogs);
//   } catch (error) {
//     console.error(error);
//     res.status(500).json({ message: 'Server error' });
//   }
// });


module.exports = router;
