import sys
import select
from sense_hat import SenseHat
from time import sleep
from random import randint

WHITE  = (255, 255, 255)
BLACK  = (  0,   0,   0)
RED    = (255,   0,   0)
GREEN  = (  0, 255,   0)
BLUE   = (  0,   0, 255)
YELLOW = (255, 255,   0)
GRAY   = ( 64,  64,  64)
PURPLE = (138,  43, 226)
ORANGE = (255, 140,   0)


""" From highest to lowerst """
BAR_COLORS = [
        (  0, 255,   0),
        (128, 255,   0),
        (192, 255,   0),
        (255, 255,   0),
        (255, 192,   0),
        (255, 128,   0),
        (255, 64,   0),
        (255,   0,   0),
]
SIZE = 8
INDIVIDUAL_FACTOR = 10
SLEEP_TIME = 0.1

CHECK = [
        [0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 1],
        [0, 0, 0, 0, 0, 0, 1, 0],
        [0, 0, 0, 0, 0, 1, 0, 0],
        [1, 0, 0, 0, 1, 0, 0, 0],
        [0, 1, 0, 1, 0, 0, 0, 0],
        [0, 0, 1, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0],
        ]

""" Multiplies a pixel by scalar k """
def mult_pixel(pixel, k):
    return tuple([int(c * k) for c in pixel])


""" Generates a gradient of SIZE colors from the given color """
def gen_gradient(color, factor = INDIVIDUAL_FACTOR):
    return [mult_pixel(color, float(factor-i)/factor) for i in range(SIZE)]


""" Class to manage the screen """
class Screen():
    def __init__(self):
        self.sense = SenseHat()
        self.general_level = 0
        self.wait_time = 4
        self.cur_time = 0
        self.clear()
        self.balance = 0

    def clear(self):
        for i in range(SIZE):
            for j in range(SIZE):
                self.sense.set_pixel(i, j, BLACK)

    def clear_col(self, x):
        for i in range(0, 7):
            self.sense.set_pixel(x, i, BLACK)

    def plot_bar(self, x, height, colors = None):
        if colors is None:
            colors = BAR_COLORS
        self.clear_col(x)
        for i in range(height):
            self.sense.set_pixel(x, 7 - i, colors[7 - i])

    def plot_balance(self):
        for i in range(SIZE):
            self.plot_bar(i, self.general_level, BAR_COLORS)

    def show_amount(self):
        self.show_message(str(self.balance), 
                          color = list(BAR_COLORS[min(7, 8 - self.general_level)]))

    def show_message(self, message, speed = 0.1, color = [255,255,255]):
        self.sense.show_message(message, speed, color)
        self.plot_balance()

    """ Parses an input in the form:
        balance percentage """
    def parse_input(self, line):
        self.cur_time = 0
        # Split balance and percentage.
        [self.balance, percent] = [float(x) for x in line.split()]
        self.general_level = int(round(percent/100.0 * SIZE))
        print(self.general_level)
        self.draw_check()

    def draw_check(self):
        types = [BLACK, GREEN]
        pixels = [types[CHECK[i/SIZE][i%SIZE]] for i in range(SIZE * SIZE)]
        self.sense.set_pixels(pixels)

    def no_text(self):
        self.cur_time += SLEEP_TIME
        if self.cur_time > self.wait_time:
            self.cur_time = 0
            self.show_amount()


if __name__ == '__main__':
    screen = Screen()
    # If there's input ready, do something, else do something
    # else. Note timeout is zero so select won't block at all.
    while True:
        while sys.stdin in select.select([sys.stdin], [], [], 0)[0]:
            line = sys.stdin.readline()
            if line:
                screen.parse_input(line)
            else: # an empty line means stdin has been closed
                print('eof')
                exit(0)
        else:
            screen.no_text()
        sleep(SLEEP_TIME)
