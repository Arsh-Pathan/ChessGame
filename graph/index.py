import matplotlib.pyplot as plt

sessions = [1, 2, 3, 4]

white_wins = [10, 16, 18, 14]
black_wins = [8, 7, 18, 7]
ties = [82, 77, 64, 79]

plt.plot(sessions, white_wins, marker='o', label='White Wins')
plt.plot(sessions, black_wins, marker='o', label='Black Wins')
plt.plot(sessions, ties, marker='o', label='Ties')

plt.xlabel('Training Session')
plt.ylabel('Number of Games')
plt.title('AI Training Results Over Time')
plt.xticks(sessions)
plt.legend()
plt.grid(True)
plt.show()
