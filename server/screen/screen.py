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


""" From highest to lowerst """
BAR_COLORS = [
        (  0, 255,   0),
        ( 64, 255,   0),
        (128, 255,   0),
        (192, 255,   0),
        (255, 255,   0),
        (255, 192,   0),
        (255, 128,   0),
        (255,   0,   0),
]
SIZE = 8


def muly_pixel(pixel, k):
    return tuple([c * k for c in pixel])


class Screen():
    def __init__(self):
        self.next_timer = 0
        self.next_total_time = 16
        self.num_dots_next = 8
        self.time_per_dot = self.next_total_time / self.num_dots_next
        self.cur_active_dots = 0
        self.sense = SenseHat()
        self.clear()

    def clear(self):
        for i in range(SIZE):
            for j in range(SIZE):
                self.sense.set_pixel(i, j, BLACK)

    def clear_col(self, x):
        for i in range(0, 7):
            self.sense.set_pixel(x, i, BLACK)

    def set_next_dot(self, i):
        self.sense.set_pixel(7, 7-i, GRAY)
        
    def step(self, amount):
        self.next_timer += amount
        act_dots = self.next_timer / self.time_per_dot
        while act_dots > self.cur_active_dots:
            self.set_next_dot(self.cur_active_dots)
            self.cur_active_dots += 1

    def plot_bar(self, x, height, colors = None):
        if colors is None:
            colors = BAR_COLORS
        self.clear_col(x)
        for i in range(height):
            self.sense.set_pixel(x, 7 - i, colors[7 - i])


if __name__ == '__main__':
    screen = Screen()
    bar_height = 0
    while True:
        screen.step(1)
        for i in range(3):
            screen.plot_bar(i, bar_height)
        if bar_height < SIZE:
            bar_height += 1
        sleep(1.0)

