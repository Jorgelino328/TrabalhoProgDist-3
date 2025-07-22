const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

const rootDir = process.env.MCP_ROOT_DIR || path.join(__dirname, '..');

app.get('/status', (req, res) => {
    res.json({
        status: 'running',
        server: 'filesystem-mcp-bridge',
        root_directory: rootDir,
        timestamp: new Date().toISOString()
    });
});

app.get('/health', (req, res) => {
    res.json({
        status: 'healthy',
        service: 'MCP Filesystem Bridge'
    });
});

app.post('/execute', (req, res) => {
    const { command, path: filePath, content } = req.body;
    
    try {
        switch (command) {
            case 'read':
                if (!filePath) {
                    return res.status(400).json({ error: 'File path required for read command' });
                }
                const fullPath = path.join(rootDir, filePath);
                if (!fs.existsSync(fullPath)) {
                    return res.status(404).json({ error: 'File not found' });
                }
                const fileContent = fs.readFileSync(fullPath, 'utf8');
                res.json({ result: fileContent, command: 'read', path: filePath });
                break;
                
            case 'write':
                if (!filePath || content === undefined) {
                    return res.status(400).json({ error: 'File path and content required for write command' });
                }
                const writeFullPath = path.join(rootDir, filePath);
                const dir = path.dirname(writeFullPath);
                if (!fs.existsSync(dir)) {
                    fs.mkdirSync(dir, { recursive: true });
                }
                fs.writeFileSync(writeFullPath, content, 'utf8');
                res.json({ result: 'File written successfully', command: 'write', path: filePath });
                break;
                
            case 'list':
                const listPath = filePath ? path.join(rootDir, filePath) : rootDir;
                if (!fs.existsSync(listPath)) {
                    return res.status(404).json({ error: 'Directory not found' });
                }
                const stats = fs.statSync(listPath);
                if (!stats.isDirectory()) {
                    return res.status(400).json({ error: 'Path is not a directory' });
                }
                const files = fs.readdirSync(listPath).map(file => {
                    const fileStats = fs.statSync(path.join(listPath, file));
                    return {
                        name: file,
                        type: fileStats.isDirectory() ? 'directory' : 'file',
                        size: fileStats.size,
                        modified: fileStats.mtime
                    };
                });
                res.json({ result: files, command: 'list', path: filePath || '/' });
                break;
                
            default:
                res.status(400).json({ error: 'Unknown command: ' + command });
        }
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

app.listen(PORT, () => {
    console.log(`MCP Filesystem Bridge running on port ${PORT}`);
    console.log(`Root directory: ${rootDir}`);
});
