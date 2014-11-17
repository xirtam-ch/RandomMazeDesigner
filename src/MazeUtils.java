import common.WallState;

import java.util.Random;

public class MazeUtils {
	private int width;
	private int height;
	private int[][] checks;

	public int[][] getChecks() {
		return MirrorHor(checks);
	}

	private WallState[][] horizontal;
	private WallState[][] vertical;
	private boolean[][] notMarked;
	private int[][] mazeData;

	public int[][] Maze(int width, int height) {
		// this.checkSize = Common.CELLW;
		this.width = width;
		this.height = height;
		// this.solve = false;
		this.checks = new int[2 * this.height + 1][2 * this.width + 1];// 迷宫为height*width个方格
		this.horizontal = new WallState[this.height + 1][this.width];
		// 单位水平墙.horizontalWalls有height*width-1个
		this.vertical = new WallState[this.height][this.width + 1];
		// 单位竖直墙.horizontalWalls有(height-1)*width个
		this.notMarked = new boolean[this.height][this.width];
		// 单位方格是否有标记过(在随机生成迷宫时需要)

		// 以下为迷宫具体初始化:
		// 全部方格状态初始化为"未踏足过"
		for (int i = 0; i < this.height; i++)
			for (int j = 0; j < this.width; j++)
				checks[i][j] = 0;

		// 全部方格初始化为"未标记"
		for (int i = 0; i < this.height; i++)
			for (int j = 0; j < this.width; j++)
				notMarked[i][j] = true;

		// 全部水平墙状态初始化为"存在"
		for (int i = 0; i < this.height + 1; i++)
			for (int j = 0; j < this.width; j++)
				horizontal[i][j] = WallState.exist;

		// 全部竖直墙状态初始化为"存在"
		for (int i = 0; i < this.height; i++)
			for (int j = 0; j < this.width + 1; j++)
				vertical[i][j] = WallState.exist;

		// 迷宫左上角方格为入口,因此它的左墙为不存在
		vertical[0][0] = WallState.notExist;

		// 迷宫右下角为出口,因此它的右墙不存在
		vertical[this.height - 1][this.width] = WallState.notExist;

		// 随机生成迷宫.其实质是:拆除若干个水平墙和竖直墙以便能形成迷宫
		generate(0, 0);

		// 生成用于绘制的数据
		mazeData = new int[height * 2 + 1][width * 2 + 1];
		for (int i = 0; i < mazeData.length; i++) {
			for (int j = 0; j < mazeData[i].length; j++) {
				if ((i & 1) == 1 && (j & 1) == 1)
					mazeData[i][j] = 0;
				else
					mazeData[i][j] = 1;

				if ((i & 1) == 0 && (j & 1) == 1) {
					if (horizontal[i / 2][(j - 1) / 2] == WallState.notExist)
						mazeData[i][j] = 0;
				}

				if ((i & 1) == 1 && (j & 1) == 0) {
					if (vertical[(i - 1) / 2][j / 2] == WallState.notExist)
						mazeData[i][j] = 0;
				}
			}
		}

		// 生成迷宫后立刻遍历,得到答案:checks[i][j]为Check.main的都是在主路径上
		// 分析Java程序设计教程书上的算法,是从首格开始的valid,但是本算法不是从首格开始.因此要从0,1开始,若不行则从1,0开始
		// if (!traverse(0, 0, 0, 1))
		traverse(1, 0, 1, 1);

		// 遍历完后可能有些不在主路径上的方格未设置成Check.notMain,最好执行以下语句
		for (int i = 0; i < 2 * this.height + 1; i++)
			for (int j = 0; j < 2 * this.width + 1; j++)
				if (checks[i][j] != 1)
					checks[i][j] = -1;

		// System.out.println("test "+valid(1, 2, 1, 3));
		// System.out.println(valid(1, 1, 2, 1));
		// System.out.println(valid(1, 1, 1, 2));

		mazeData = MirrorHor(mazeData);
		return mazeData;
		// return MirrorHor(mazeData);
	}

	/**
	 * 生成一个随机迷宫,本方法提供给构造方法及newMaze方法调用. 实质是:拆除若干个水平墙和竖直墙.算法来源:java2游戏设计,清华大学出版社
	 *
	 * @param i
	 *            可以是0至height-1中任一数值,建议输入0
	 * @param j
	 *            可以是0至width-1中任一数值,建议输入0
	 */
	private void generate(int i, int j) {
		notMarked[i][j] = false;// 标记这个方格,以便不再访问

		Random gen = new Random();// 用于随机一个方向

		if (i == 0 && j == 0)// 左上角方格
			while (notMarked[i + 1][j] || notMarked[i][j + 1])// 只要下面的方格和右边方格有一个未标记就进入循环
			{
				switch (gen.nextInt(2)) {
					case 0:
						if (notMarked[i + 1][j])// 下面方格未标记
						{
							horizontal[i + 1][j] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i + 1, j);
						}// 继续从此方格开始生成
						break;
					case 1:
						if (notMarked[i][j + 1])// 右边方格未标记
						{
							vertical[i][j + 1] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i, j + 1);
						}// 继续从此方格开始生成
						break;
				}
			}

		if (i == this.height - 1 && j == 0)// 左下角方格
			while (notMarked[i - 1][j] || notMarked[i][j + 1])// 只要上面的方格和右边方格有一个未标记就进入循环
			{
				switch (gen.nextInt(2)) {
					case 0:
						if (notMarked[i - 1][j])// 上面方格未标记
						{
							horizontal[i][j] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i - 1, j);
						}// 继续从此方格开始生成
						break;
					case 1:
						if (notMarked[i][j + 1])// 右边方格未标记
						{
							vertical[i][j + 1] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i, j + 1);
						}// 继续从此方格开始生成
						break;
				}
			}

		// 以下算法同上,不详述.总之是分类:四个角,四条边,其余.

		if (i == 0 && j == this.width - 1)// 右上角方格
			while (notMarked[i + 1][j] || notMarked[i][j - 1]) {
				switch (gen.nextInt(2)) {
					case 0:
						if (notMarked[i + 1][j]) {
							horizontal[i + 1][j] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i + 1, j);
						}// 继续从此方格开始生成
						break;
					case 1:
						if (notMarked[i][j - 1]) {
							vertical[i][j] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i, j - 1);
						}// 继续从此方格开始生成
						break;
				}
			}

		if (i == this.height - 1 && j == this.width - 1)// 右下角方格
			while (notMarked[i - 1][j] || notMarked[i][j - 1]) {
				switch (gen.nextInt(2)) {
					case 0:
						if (notMarked[i - 1][j]) {
							horizontal[i][j] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i - 1, j);
						}// 继续从此方格开始生成
						break;
					case 1:
						if (notMarked[i][j - 1]) {
							vertical[i][j] = WallState.notExist;// 拆除它们之间的墙,使它们连通
							generate(i, j - 1);
						}// 继续从此方格开始生成
						break;
				}
			}

		if (i == 0 && j != 0 && j != this.width - 1)// 第一行方格除了左上角和右上角
			while (notMarked[i][j + 1] || notMarked[i][j - 1]
					|| notMarked[i + 1][j]) {
				switch (gen.nextInt(3)) {
					case 0:
						if (notMarked[i][j + 1]) {
							vertical[i][j + 1] = WallState.notExist;
							generate(i, j + 1);
						}
						break;
					case 1:
						if (notMarked[i][j - 1]) {
							vertical[i][j] = WallState.notExist;
							generate(i, j - 1);
						}
						break;
					case 2:
						if (notMarked[i + 1][j]) {
							horizontal[i + 1][j] = WallState.notExist;
							generate(i + 1, j);
						}
						break;
				}
			}

		if (i == this.height - 1 && j != 0 && j != this.width - 1)// 最后一行方格除了左下角和右下角
			while (notMarked[i][j - 1] || notMarked[i][j + 1]
					|| notMarked[i - 1][j]) {
				switch (gen.nextInt(3)) {
					case 0:
						if (notMarked[i][j - 1]) {
							vertical[i][j] = WallState.notExist;
							generate(i, j - 1);
						}
						break;
					case 1:
						if (notMarked[i][j + 1]) {
							vertical[i][j + 1] = WallState.notExist;
							generate(i, j + 1);
						}
						break;
					case 2:
						if (notMarked[i - 1][j]) {
							horizontal[i][j] = WallState.notExist;
							generate(i - 1, j);
						}
						break;
				}
			}

		if (j == 0 && i != 0 && i != this.height - 1)// 第一列方格除了左上角和左下角
			while (notMarked[i - 1][j] || notMarked[i + 1][j]
					|| notMarked[i][j + 1]) {
				switch (gen.nextInt(3)) {
					case 0:
						if (notMarked[i - 1][j]) {
							horizontal[i][j] = WallState.notExist;
							generate(i - 1, j);
						}
						break;
					case 1:
						if (notMarked[i + 1][j]) {
							horizontal[i + 1][j] = WallState.notExist;
							generate(i + 1, j);
						}
						break;
					case 2:
						if (notMarked[i][j + 1]) {
							vertical[i][j + 1] = WallState.notExist;
							generate(i, j + 1);
						}
						break;
				}
			}

		if (j == this.width - 1 && i != 0 && i != this.height - 1)// 最后一列方格除了右上角和右下角
			while (notMarked[i - 1][j] || notMarked[i + 1][j]
					|| notMarked[i][j - 1]) {
				switch (gen.nextInt(3)) {
					case 0:
						if (notMarked[i - 1][j]) {
							horizontal[i][j] = WallState.notExist;
							generate(i - 1, j);
						}
						break;
					case 1:
						if (notMarked[i + 1][j]) {
							horizontal[i + 1][j] = WallState.notExist;
							generate(i + 1, j);
						}
						break;
					case 2:
						if (notMarked[i][j - 1]) {
							vertical[i][j] = WallState.notExist;
							generate(i, j - 1);
						}
						break;
				}
			}

		if (i > 0 && i < this.height - 1 && j > 0 && j < this.width - 1)// 除了最外面一层方格外的其余方格(即有(height-2)*(width-2)个)
			while (notMarked[i - 1][j] || notMarked[i + 1][j]
					|| notMarked[i][j - 1] || notMarked[i][j + 1]) {
				switch (gen.nextInt(4)) {
					case 0:
						if (notMarked[i - 1][j]) {
							horizontal[i][j] = WallState.notExist;
							generate(i - 1, j);
						}
						break;
					case 1:
						if (notMarked[i + 1][j]) {
							horizontal[i + 1][j] = WallState.notExist;
							generate(i + 1, j);
						}
						break;
					case 2:
						if (notMarked[i][j - 1]) {
							vertical[i][j] = WallState.notExist;
							generate(i, j - 1);
						}
						break;
					case 3:
						if (notMarked[i][j + 1]) {
							vertical[i][j + 1] = WallState.notExist;
							generate(i, j + 1);
						}
						break;
				}
			}
	}

	/**
	 * 遍历迷宫,遍历完成后就能找到主路径:checks[i][j]为Check.main的方格都在主路径上
	 *
	 * @param i1
	 *            原方格所在行数
	 * @param j1
	 *            原方格所在列数
	 * @param i2
	 *            目标方格所在行数
	 * @param j2
	 *            目标方格所在列数
	 * @return 找到从起点到终点路径则返回true;否则返回false
	 */
	private boolean traverse(int i1, int j1, int i2, int j2) {
		boolean done = false;

		if (i1 == 1 && j1 == 0)// 起点
			checks[i1][j1] = 1;

		if (valid(i1, j1, i2, j2)) {
			checks[i2][j2] = -1;// 先设定为notMain表示不在主路径上

			if (i2 == 2 * this.height - 1 && j2 == 2 * this.width)
				done = true; // 到达终点
			else {
				done = traverse(i2, j2, i2 + 1, j2); // down
				if (!done)
					done = traverse(i2, j2, i2, j2 + 1); // right
				if (!done)
					done = traverse(i2, j2, i2 - 1, j2); // up
				if (!done)
					done = traverse(i2, j2, i2, j2 - 1); // left
			}

			if (done) // 若done为true说明这个方格在主路径上
				checks[i2][j2] = 1;
		} else {
			// System.out.println("from " + i1 + "," + j1 + " to " + i2 + "," +
			// j2
			// + "is not valid");
		}

		return done;
	}

	/**
	 * 本方法供traverse方法调用,用于判断从i1行j1列的方格走到i2行j2列的方格是否可行
	 *
	 * @param i1
	 *            原方格所在行数
	 * @param j1
	 *            原方格所在列数
	 * @param i2
	 *            目标方格所在行数
	 * @param j2
	 *            目标方格所在列数
	 * @return 从原格走到目标格可行则返回true;否则返回false
	 */
	private boolean valid(int i1, int j1, int i2, int j2) {
		boolean result = false;

		if (i2 >= 0 && i2 < 2 * this.height + 1 && j2 >= 0
				&& j2 < 2 * this.width + 1)
			if (checks[i2][j2] == 0) {// 保证没有越界而且目标方格未踏足过
				if (i1 == i2 - 1 && mazeData[i2][j2] == 0)// 从上往下
					result = true;
				else if (i1 == i2 + 1 && mazeData[i1][j1] == 0)// 从下往上
					result = true;
				else if (j1 == j2 - 1 && mazeData[i2][j2] == 0)// 从左往右
					result = true;
				else if (j1 == j2 + 1 && mazeData[i1][j1] == 0)// 从右往左
					result = true;
			}
		return result;
	}

	public static void print(int[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				System.out.print(arr[i][j] < 0 ? arr[i][j] : " " + arr[i][j]);
			}
			System.out.println(" ");
		}
	}

	public static int[][] arrCopy(int[][] arr) {
		int[][] tmp = new int[arr.length][arr[0].length];
		for (int i = 0; i < tmp.length; i++) {
			for (int j = 0; j < tmp.length; j++) {
				tmp[i][j] = arr[i][j];
			}
		}
		return tmp;
	}

	public static int getCount(int[][] data, int type) {
		int count = 0;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				if (data[i][j] == type)
					count++;
			}
		}
		return count;
	}

	public static int[][] MirrorHor(int[][] data) {
		for (int i = 0; i < data.length; i++) {
			int len = data[i].length;
			for (int j = 0; j < len / 2; j++) {
				if (j != len - 1 - j) {
					data[i][j] = data[i][j] ^ data[i][len - 1 - j];
					data[i][len - 1 - j] = data[i][j] ^ data[i][len - 1 - j];
					data[i][j] = data[i][j] ^ data[i][len - 1 - j];
				}
			}
		}
		return data;
	}
}
