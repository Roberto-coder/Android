const WebSocket = require('ws');
const fs = require('fs');
const path = require('path');

const wss = new WebSocket.Server({ port: 3000 });

let gameState = {
  players: [],
  currentPlayerIndex: 0,
  gameData: {}
};

// Load or create initial game data
const initialGameDataPath = path.join(__dirname, 'initialGameData.json');
if (fs.existsSync(initialGameDataPath)) {
  gameState.gameData = JSON.parse(fs.readFileSync(initialGameDataPath, 'utf8'));
} else {
  gameState.gameData = createInitialGameData();
  saveGameData(gameState.gameData, 'defaultPlayer');
}

wss.on('connection', (ws) => {
  ws.on('message', (message) => {
    const data = JSON.parse(message);
    handleClientMessage(ws, data);
  });

  ws.on('close', () => {
    handleDisconnect(ws);
  });
});

function createInitialGameData() {
  return {
    myBoard: Array(10).fill().map(() => Array(10).fill(0)),
    myShotsBoard: Array(10).fill().map(() => Array(10).fill(0)),
    shipsToPlace: [
      { length: 2, isHorizontal: false },
      { length: 3, isHorizontal: false },
      { length: 4, isHorizontal: true }
    ],
    currentPlayerIndex: 0,
    isTurn: true,
    gameState: "coloca tus barcos"
  };
}

function saveGameData(gameData, playerName) {
  const date = new Date();
  const formattedDate = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}_${date.getHours().toString().padStart(2, '0')}-${date.getMinutes().toString().padStart(2, '0')}-${date.getSeconds().toString().padStart(2, '0')}`;
  const playerDir = path.join(__dirname, 'public', playerName);
  const filePath = path.join(playerDir, `${formattedDate}.json`);

  // Ensure the player directory exists
  if (!fs.existsSync(playerDir)) {
    fs.mkdirSync(playerDir, { recursive: true });
  }

  fs.writeFileSync(filePath, JSON.stringify(gameData));
}

function handleClientMessage(ws, data) {
  switch (data.action) {
    case 'CONNECT':
      handleConnect(ws, data.playerName);
      break;
    case 'END_TURN':
      handleEndTurn(ws, data.playerName, data.gameData);
      break;
    default:
      ws.send(JSON.stringify({ action: 'ERROR', message: 'Unknown action' }));
  }
}

function handleConnect(ws, playerName) {
  if (gameState.players.length < 2) {
    gameState.players.push({ ws, playerName, isReady: false });
    ws.send(JSON.stringify({ action: 'INITIAL_GAME_STATE', gameState: gameState.gameData, flag: 'coloca tus barcos' }));
    if (gameState.players.length === 2) {
      startGame();
    }
  } else {
    ws.send(JSON.stringify({ action: 'ERROR', message: 'Game is full' }));
  }
}

function handleEndTurn(ws, playerName, gameData) {
  const playerIndex = gameState.players.findIndex(player => player.ws === ws);
  if (playerIndex === gameState.currentPlayerIndex) {
    gameState.gameData = JSON.parse(gameData);
    gameState.currentPlayerIndex = (gameState.currentPlayerIndex + 1) % 2;
    saveGameData(gameState.gameData, playerName);
    broadcastGameState();
    checkGameOver();
  } else {
    ws.send(JSON.stringify({ action: 'ERROR', message: 'Not your turn' }));
  }
}

function handleDisconnect(ws) {
  const player = gameState.players.find(player => player.ws === ws);
  gameState.players = gameState.players.filter(player => player.ws !== ws);
  if (gameState.players.length < 2) {
    resetGame(player.playerName);
  }
}

function startGame() {
  gameState.players.forEach(player => {
    player.ws.send(JSON.stringify({ action: 'coloca tus barcos' }));
  });
}

function broadcastGameState() {
  gameState.players.forEach((player, index) => {
    const flag = gameState.gameData.shipsToPlace.length > 0 ? 'coloca tus barcos' : (index === gameState.currentPlayerIndex ? 'envia un misil' : 'espera tu turno');
    player.ws.send(JSON.stringify({ action: 'UPDATE_FLAGS', gameState: gameState.gameData, flag }));
  });
}

function checkGameOver() {
  // Game over logic
}

function resetGame(playerName) {
  gameState = {
    players: [],
    currentPlayerIndex: 0,
    gameData: createInitialGameData()
  };
  saveGameData(gameState.gameData, playerName);
}

console.log('Server is running on ws://localhost:3000');