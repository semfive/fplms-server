const jwt = require("jsonwebtoken");

async function validateToken(req, res, next) {
  const token = req.headers["authorization"];
  if (!token) {
    res.writeHead(401);
    res.write("Token not found.");
    return res.end();
  }
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.headers["user"] = decoded;
  } catch (err) {
    res.writeHead(401);
    res.write("Invalid token.");
    return res.end();
  }
  return next();
}

async function validateOrigin(req, res, next) {
  const origin = req.headers["host"];
  if ([process.env.DISCUSSION_SERVICE].indexOf(origin) === -1) {
    res.writeHead(403);
    res.write("Access to resource denied.");
    return res.end();
  }
  return next();
}

export { validateToken, validateOrigin };
