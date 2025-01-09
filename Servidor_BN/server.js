const WebSocket = require('ws');
const fs = require('fs');
const path = require('path');

const wss = new WebSocket.Server({ port: 3000 });

let gameState = {
  players: [],
  currentPlayerIndex: 0
};

// Load or create initial game data
const gameDataPath = path.join(__dirname, 'gameData.json');
let initialGameData;
if (fs.existsSync(gameDataPath)) {
  initialGameData = JSON.parse(fs.readFileSync(gameDataPath, 'utf8'));
} else {
  initialGameData = createInitialGameData();
  saveGameData(initialGameData, 'defaultPlayer');
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
    myBoard: Array(6).fill().map(() => Array(6).fill(0)),
    myShotsBoard: Array(6).fill().map(() => Array(6).fill(0)),
    shipsToPlace: [
      { length: 2, isHorizontal: false },
      { length: 3, isHorizontal: false },
    ],
    currentPlayerIndex: 0,
    isTurn: true,
    placeShipsFlag: 1,
    shipsPlacedCount: 0,
    missilesFiredCount: 0
  };
}

function saveGameData(gameData, playerName) {
  const date = new Date();
  const formattedDate = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}_${date.getHours().toString().padStart(2, '0')}-${date.getMinutes().toString().padStart(2, '0')}-${date.getSeconds().toString().padStart(2, '0')}`;
  const playerDir = path.join(__dirname, 'public', playerName);
  if (!fs.existsSync(playerDir)) {
    fs.mkdirSync(playerDir, { recursive: true });
  }
  const filePath = path.join(playerDir, `${formattedDate}.json`);
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
    case 'FIRE_MISSILE':
      handleFireMissile(ws, data.row, data.col);
      break;
    default:
      ws.send(JSON.stringify({ action: 'ERROR', message: 'Unknown action' }));
  }
}

function handleConnect(ws, playerName) {
  if (gameState.players.length < 2) {
    gameState.players.push({ ws, playerName, isReady: false, ...createInitialGameData() });
    ws.send(JSON.stringify({ action: 'INITIAL_GAME_STATE', ...createInitialGameData(), flag: 'coloca tus barcos' }));
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
    const parsedGameData = JSON.parse(gameData);
    Object.assign(gameState.players[playerIndex], parsedGameData);
    gameState.players[playerIndex].isTurn = false; // Set isTurn to false for the current player
    gameState.currentPlayerIndex = (gameState.currentPlayerIndex + 1) % 2;
    gameState.players[gameState.currentPlayerIndex].isTurn = true; // Set isTurn to true for the next player
    saveGameData(parsedGameData, playerName);

    gameState.players.forEach((player, index) => {
      const flag = index === gameState.currentPlayerIndex ? 'envia un misil' : 'espera tu turno';
      player.ws.send(JSON.stringify({ action: 'UPDATE_FLAGS', ...gameState.players[index], flag }));
    });

    if (gameState.players.every(player => player.missilesFiredCount > 0)) {
      checkGameOver();
    }
  } else {
    ws.send(JSON.stringify({ action: 'ERROR', message: 'Not your turn' }));
  }
}

function handleFireMissile(ws, row, col) {
  const playerIndex = gameState.players.findIndex(player => player.ws === ws);
  if (playerIndex === -1) {
    ws.send(JSON.stringify({ action: 'ERROR', message: 'Player not found' }));
    return;
  }

  const opponentIndex = (playerIndex + 1) % 2;
  const opponentBoard = gameState.players[opponentIndex].myBoard;

  if (opponentBoard[row][col] === 2) {
    gameState.players[playerIndex].myShotsBoard[row][col] = 1;
    opponentBoard[row][col] = 1;
    ws.send(JSON.stringify({ action: 'HIT', message: `Hit at (${row}, ${col})` }));
  } else {
    gameState.players[playerIndex].myShotsBoard[row][col] = 1;
    ws.send(JSON.stringify({ action: 'MISS', message: `Miss at (${row}, ${col})` }));
  }

  gameState.players[playerIndex].missilesFiredCount += 1;
  saveGameData(gameState.players[playerIndex], gameState.players[playerIndex].playerName);
  broadcastGameState();

  if (gameState.players.every(player => player.missilesFiredCount > 0)) {
    checkGameOver();
  }
}

function handleDisconnect(ws) {
  const player = gameState.players.find(player => player.ws === ws);
  gameState.players = gameState.players.filter(player => player.ws !== ws);
  if (gameState.players.length < 2) {
    resetGame(player ? player.playerName : 'defaultPlayer');
  }
}

function startGame() {
  gameState.players.forEach(player => {
    player.ws.send(JSON.stringify({ action: 'coloca tus barcos' }));
  });
}

function broadcastGameState() {
  gameState.players.forEach((player, index) => {
    const flag = player.shipsToPlace.length > 0 ? 'coloca tus barcos' : (index === gameState.currentPlayerIndex ? 'envia un misil' : 'espera tu turno');
    player.ws.send(JSON.stringify({ action: 'UPDATE_FLAGS', ...player, flag }));
  });
}

function checkGameOver() {
  const allShipsSunk = gameState.players.map((player, playerIndex) => {
    const opponentIndex = (playerIndex + 1) % 2;
    const opponentShotsBoard = gameState.players[opponentIndex].myShotsBoard;

    return player.myBoard.every((row, rowIndex) =>
      row.every((cell, colIndex) => cell !== 2 || opponentShotsBoard[rowIndex][colIndex] === 1)
    );
  });

  if (allShipsSunk[0] && allShipsSunk[1]) {
    // Both players' ships are sunk, it's a draw
    gameState.players.forEach(player => {
      player.ws.send(JSON.stringify({ action: 'GAME_OVER', message: 'Draw' }));
    });
    resetGame(gameState.players[0].playerName);
  } else if (allShipsSunk[0]) {
    gameState.players[0].ws.send(JSON.stringify({ action: 'GAME_OVER', message: 'You lost' }));
    gameState.players[1].ws.send(JSON.stringify({ action: 'GAME_OVER', message: 'You won' }));
    resetGame(gameState.players[0].playerName);
  } else if (allShipsSunk[1]) {
    gameState.players[1].ws.send(JSON.stringify({ action: 'GAME_OVER', message: 'You lost' }));
    gameState.players[0].ws.send(JSON.stringify({ action: 'GAME_OVER', message: 'You won' }));
    resetGame(gameState.players[1].playerName);
  }
}

function resetGame(playerName) {
  gameState = {
    players: [],
    currentPlayerIndex: 0
  };
  saveGameData(createInitialGameData(), playerName);
}

console.log('Server is running on ws://localhost:3000');