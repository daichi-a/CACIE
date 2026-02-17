package CACIE.ui.ShoppingBasketGUI;

import processing.core.PApplet;

public class IndividualIcon implements Runnable {

	float FRICTION = (float) 0.98;
	float BrakeCoefficient = (float) 0.6;
	float x, y;
	float Spx, Spy;
	int radius, currentRadius;// if this individual is apported, radius
	// becomes smaller;
	int areaWidth, areaHeight; // Width and Height of current area
	int areaOffsetX, areaOffsetY; // position of the current area
	int areaID;
	int index;
	boolean onDrag;
	int colorHue;

	boolean onApported; //Apportされている
	boolean onApportCenter; //Apportしている
	IndividualIcon apportTarget;
	int apportContainPosition; // 引きずられる側がターゲットのどこに格納されるか
	int[] apportContainSpace; // 引き連れている側がどのインデックスの個体を引き連れているか

	// アニメーション用
	double radiusOffset;
	int brightnessOffset;
	int frameCounter;

	boolean processedInThisStep;

	ShoppingBasket_IEC shoppingBasket;

	protected IndividualIcon(ShoppingBasket_IEC shoppingBasket, int index,
			int width, int height, int offsetX, int offsetY, int areaID,
			int radius, int hue) {
		// width, height: spaces of the field
		// hue: hue of Color /0-99
		this.shoppingBasket = shoppingBasket;

		this.index = index;
		this.radius = radius;
		areaWidth = width;
		areaHeight = height;
		areaOffsetX = offsetX;
		areaOffsetY = offsetY;
		this.areaID = areaID;
		colorHue = hue;
		// Set initialValues
		x = shoppingBasket.random(radius, areaWidth - radius);
		y = shoppingBasket.random(radius, areaHeight - radius);
		Spx = shoppingBasket.random((float) -2.0, (float) 2.0);
		Spy = shoppingBasket.random((float) -2.0, (float) 2.0);

		onDrag = false;
		frameCounter = 0;

		onApported = false;
		onApportCenter = false;
		apportTarget = this;
		apportContainPosition = -1;
		apportContainSpace = new int[shoppingBasket.numOfIndividual];
		initializeApportSpace();
		processedInThisStep = false;
	}

	public void setProcessedInThisStep(boolean t) {
		processedInThisStep = t;
	}

	public void run() {
		draw();
	}

	public void draw() {
		if (processedInThisStep != true) {
			// Calcurate the Position
			if (onDrag)
				dragging();
			else if (onApported && apportTarget.areaID == areaID)
				apportMoving();
			else{
				int bouncedOtherIndex = moving();
				if(bouncedOtherIndex != -1)
				shoppingBasket.iconArray[bouncedOtherIndex].setProcessedInThisStep(true);
			}
		}
		
		shoppingBasket.noFill();
		shoppingBasket.stroke(colorHue, 60, 100);
		shoppingBasket.strokeWeight(3);

		if (checkOnMouse())
			shoppingBasket.fill(colorHue, 100, 100, 50);
		else
			shoppingBasket.fill(colorHue, 40, 40, 50);

		radiusOffset = 0;
		brightnessOffset = 0;

		if (onApported && checkOnMouse()
				&& shoppingBasket.lastPlayedIndex != index) {
			// マウスオーバーされた時，Apportの状態かつ現在自分が再生中でなければ，新たに再生を開始する
			// When mouse-overed, if this icon is on drag && in playing
			// music,
			// start play-back.
			shoppingBasket.gppe.stopAll();
			shoppingBasket.gppe.playAsMIDISequence(index);
			shoppingBasket.lastPlayedIndex = index;
			//System.err.println("Play Music: " + index);
		}

		if (shoppingBasket.lastClickedIconIndex != -1) {
			if (shoppingBasket.gppe.getPlayingState(index)) {
				// 再生を行う
				// Playing
				double sinValue = shoppingBasket.getSinArray(frameCounter);
				int defaultSaturation = 100; // 白?
				int defaultBrightness = 70;

				frameCounter++;
				if (frameCounter > 19)
					frameCounter = 0;

				radiusOffset = sinValue * 10.0;
				brightnessOffset = (int) Math.floor(sinValue * 30.0);

				shoppingBasket.fill(colorHue, defaultSaturation,
						defaultBrightness + brightnessOffset, 50);
			}
		}
		if (onApported && apportTarget.areaID == areaID)
			currentRadius = radius / 2;
		else
			currentRadius = radius;
		//System.err.println("Draw Ellipse");
		shoppingBasket.ellipse(x, y,
				(float) (currentRadius + radiusOffset) * 2,
				(float) (currentRadius + radiusOffset) * 2);

		processedInThisStep = true;
	}

	public float[] getPosition() {
		float returnPosition[] = { x, y };
		return returnPosition;
	}

	public void setPosition(float newX, float newY) {
		x = newX;
		y = newY;
	}

	public void setSpeed(float newSpx, float newSpy) {
		Spx = newSpx;
		Spy = newSpy;
	}

	public void initializeApportSpace() {
		for (int i = 0; i < shoppingBasket.numOfIndividual; i++)
			apportContainSpace[i] = -1;
	}

	public float getRadius() {
		return currentRadius;
	}

	public void setDragFlag(boolean onDrag) {
		this.onDrag = onDrag;
	}

	public void setApportedFlag(boolean onApport) {
		this.onApported = onApport;
		for (int i = 0; i < 8; i++)
			apportContainSpace[i] = -1;
	}

	public void setApportCenterFlag(boolean onApportCenter){
		this.onApportCenter = onApportCenter;
	}
	
	public void setApportTarget(IndividualIcon apportTarget) {
		this.apportTarget = apportTarget;
	}

	protected void setArea(int width, int height, int offsetX, int offsetY,
			int areaID) {
		areaWidth = width;
		areaHeight = height;
		areaOffsetX = offsetX;
		areaOffsetY = offsetY;
		this.areaID = areaID;
	}

	public int getAreaID() {
		return areaID;
	}

	public int getIndex() {
		return index;
	}

	protected int moving() {
		onApported = false;
		if ((Spx > 2.0 && Spx < 5.0) || (Spx < -2.0 && Spx > -5.0))
			Spx = Spx * FRICTION;
		else if (Spx >= 5.0 || Spx <= -5.0)
			Spx = Spx * BrakeCoefficient;
		else if (Spx < 0.01 && Spx > -0.01)
			Spx = shoppingBasket.random((float) -0.5, (float) 0.5);

		if ((Spy > 2.0 && Spy < 5.0) || (Spy < -2.0 && Spy > -5.0))
			Spy = Spy * FRICTION;
		else if (Spy >= 5.0 || Spy <= -5.0)
			Spy = Spy * BrakeCoefficient;
		else if (Spy < 0.01 && Spy > -0.01)
			Spy = shoppingBasket.random((float) -0.5, (float) 0.5);
		x = x + Spx;
		y = y + Spy;
		bounce();
		int overrappedTargetIndex = checkOverrapWithOtherIndividual();
		if (overrappedTargetIndex != -1) {
			bounceWithOthers(overrappedTargetIndex);
		}
		return overrappedTargetIndex;
	}

	public void setApportedPosition() {
		// Apport状態でエリアチェンジなどが起きた時に強制的にdrawする
		int apportedPositionIndex = apportTarget.getContainSpaceIndex(this);
		if (apportedPositionIndex != -1) {
			float targetPoints[] = getApportedPosition(apportedPositionIndex);
			x = targetPoints[0];
			y = targetPoints[1];
		}
		// change the Area information
		if (this.areaID != apportTarget.getAreaID())
			changeAreaInformations(apportTarget.getAreaID());
	}

	protected void apportMoving() {
		// System.err.println(index + " is Apported by :" +apportTarget.index);
		// float targetPosition[] = apportTarget.getPosition();
		// 目的地targetIconのポジションの右隣
		// 即ちx=targetX+targetRadius+thisRadius, y=targetY

		// 8回のdraw()で目的近くまで減速しながら移動する
		// ある一定距離距離が50以下まで到達したらくっつく
		// くっついたあとはそのまま引きずられて移動する

		int apportedPositionIndex = apportTarget.getContainSpaceIndex(this);
		if (apportedPositionIndex != -1) {
			float targetPoints[] = getApportedPosition(apportedPositionIndex);
			float targetX = targetPoints[0];
			float targetY = targetPoints[1];

			if (PApplet.dist(targetX, targetY, x, y) < 50.0) {
				// 近けりゃくっつく
				x = targetX;
				y = targetY;
			} else {
				// 遠ければ減速しながら近づく
				if (x > targetX)
					x = targetX + (x - targetX) * (float) 0.8;
				else
					x = targetX - (targetX - x) * (float) 0.8;
				if (y > targetY)
					y = targetY + (y - targetY) * (float) 0.8;
				else
					y = targetY - (targetY - y) * (float) 0.8;
				// System.err.println("Index:" + index + " is apported by :"
				// + apportTarget.index + ": move to " + x + "," + y);
			}
		//} else {
			//	System.err.println("apportMoving:getContainSpaceIndex returns -1");
		}

		// System.err.println("ApportMoving,Index:"+index + ",X:Y=" + x +":"+y);
	}

	protected float[] getApportedPosition(int containSpaceIndex) {
		float returnPoints[] = new float[2];
		float basePosition[] = apportTarget.getPosition();
		switch (containSpaceIndex) {
		case 0:
			returnPoints[0] = basePosition[0];
			returnPoints[1] = basePosition[1] - apportTarget.getRadius()
					- radius;
			break;
		case 1:
			returnPoints[0] = basePosition[0];
			returnPoints[1] = basePosition[1] + apportTarget.getRadius()
					+ radius;
			break;
		case 2:
			returnPoints[0] = basePosition[0] - apportTarget.getRadius()
					- radius;
			returnPoints[1] = basePosition[1];
			break;
		case 3:
			returnPoints[0] = basePosition[0] + apportTarget.getRadius()
					+ radius;
			returnPoints[1] = basePosition[1];
			break;
		case 4:
			returnPoints[0] = basePosition[0]
					+ (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					+ (float) Math.sqrt(radius * radius / 2);
			returnPoints[1] = basePosition[1]
					- (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					- (float) Math.sqrt(radius * radius / 2);
			break;
		case 5:
			returnPoints[0] = basePosition[0]
					- (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					- (float) Math.sqrt(radius * radius / 2);
			returnPoints[1] = basePosition[1]
					+ (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					+ (float) Math.sqrt(radius * radius / 2);
			break;
		case 6:
			returnPoints[0] = basePosition[0]
					+ (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					+ (float) Math.sqrt(radius * radius / 2);
			returnPoints[1] = basePosition[1]
					+ (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					+ (float) Math.sqrt(radius * radius / 2);
			break;
		case 7:
			returnPoints[0] = basePosition[0]
					- (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					- (float) Math.sqrt(radius * radius / 2);
			returnPoints[1] = basePosition[1]
					- (float) Math.sqrt(apportTarget.getRadius()
							* apportTarget.getRadius() / 2)
					- (float) Math.sqrt(radius * radius / 2);
			break;
		default:
			break;
		}

		return returnPoints;
	}

	protected void dragging() {
		if(onApported){
			onApported = false;
			int counter=0;
			while(counter < 8){
				if(apportTarget.apportContainSpace[counter] == index){
					apportTarget.apportContainSpace[counter] = -1;
					break;
				}
				counter++;
			}
		}
		
		x = shoppingBasket.mouseX;
		y = shoppingBasket.mouseY;
		Spx = shoppingBasket.mouseX - shoppingBasket.pmouseX;
		Spy = shoppingBasket.mouseY - shoppingBasket.pmouseY;
	}

	protected void bounce() {
		// Refrection with area wall and other individual icon

		float bounceMinX = radius + areaOffsetX;
		float bounceMaxX = areaWidth + areaOffsetX - radius;
		float bounceMinY = radius + areaOffsetY;
		float bounceMaxY = areaHeight + areaOffsetY - radius;

		// X
		if (x < bounceMinX || x > bounceMaxX) {
			// Wall
			if (Spx < 5.0 || Spx > -5.0)
				Spx = -Spx * FRICTION; // Negative Speed
			else
				Spx = -Spx * BrakeCoefficient;

			if (Math.abs(Spx) < 1)
				Spx = 0; // Stop moving;
			if (x < bounceMinX)
				x = bounceMinX - (x - bounceMinX);
			if (x > bounceMaxX)
				x = bounceMaxX - (x - bounceMaxX);
		}

		// Y
		if (y < bounceMinY || y > bounceMaxY) {
			Spy = -Spy * FRICTION; // Negative Speed
			if (Math.abs(Spy) < 1)
				Spy = 0; // Stop moving;
			if (y < bounceMinY)
				y = bounceMinY - (y - bounceMinY);
			if (y > bounceMaxY)
				y = bounceMaxY - (y - bounceMaxY);
		}
	}

	protected void bounceWithOthers(int overrappedIndex) {
		IndividualIcon overrapped = shoppingBasket.iconArray[overrappedIndex];
		float targetPosition[] = overrapped.getPosition();
		float targetX = targetPosition[0], targetY = targetPosition[1];
		float targetRadius = overrapped.getRadius();
		float targetSpx = overrapped.Spx;
		float targetSpy = overrapped.Spy;
		float distanceOfCenters = PApplet.dist(x, y, targetX, targetY);
		float merikomi = radius + targetRadius - distanceOfCenters;

		// このIndividual移動方向のベクトルの射影を行う
		// targetの円の中心座標からこの円の中心座標のベクトルをもとめて正規かする
		float x1 = x - targetX;
		float y1 = y - targetY;
		float length = (float) Math.sqrt((x1 * x1) + (y1 * y1));
		if (length > 0)
			length = (float) 1.0 / length;
		x1 = x1 * length;
		y1 = y1 * length;
		// targetの円の補正方向を計算する．上記のベクトルを反転する
		float x2 = -x1;
		float y2 = -y1;
		// 二つの円の補正係数を決める．ごうけいが１になるような値にする．
		// 0に近いほど固定され，１に近いほど押される．
		float d1 = (float) 0.5, d2 = (float) 0.5;
		// 補正方向にめり込み分スケーリングして座標に足し込みをする．
		x += x1 * merikomi * d1;
		y += y1 * merikomi * d1;
		targetX += x2 * merikomi * d2;
		targetY += y2 * merikomi * d2;

		// 反射方向の計算
		// 両方の円の重心を結ぶ線に垂直な線に対して反射する
		// この円の反射ベクトルを求めて正規化する
		float t = -(x1 * Spx + y1 * Spy) / (x1 * x1 + y1 * y1);
		x1 += t * x1 * 2;
		y1 += t * y1 * 2;
		length = (float) Math.sqrt((x * x) + (y * y));
		if (length > 0)
			length = 1;
		x1 = x1 * length;
		y1 = y1 * length;
		// targetの円の反射方向を求めて正規化する
		t = -(x2 * targetSpx + y2 * targetSpy) / (x2 * x2 + y2 + y2);
		x2 += t * x2 * 2;
		y2 += t * y2 * 2;
		length = (float) Math.sqrt(x2 * x2 + y2 * y2);
		if (length > 0)
			length = 1 / length;
		x2 = x2 * length;
		y2 = y2 * length;

		// 二つの円の移動量の合計を出す
		// float idou = (float)Math.sqrt(Spx*Spx+Spy*Spy) +
		// (float)Math.sqrt(targetSpx*targetSpx+targetSpy*targetSpy);

		// 反射方向に移動量の合計でスケーリングして新たな移動量とする
		// Spx = x1 * idou * d1;
		// Spy = y1 * idou * d1;
		// targetSpx = x2 * idou * d2;
		// targetSpy = y2 * idou * d2;
		Spx = x1;
		Spy = y1;
		targetSpx = x2;
		targetSpy = y2;

		// targetに情報を渡す
		overrapped.setPosition(targetX, targetY);
		overrapped.setSpeed(targetSpx, targetSpy);
	}

	protected int checkOverrapWithOtherIndividual() {
		// If case this icon overrap with other individual
		// return index of the overraped individual
		// else return -1;
		// Argument is "X" or "Y"
		int returnIndex = -1;
		for (int i = 0; i < shoppingBasket.numOfIndividual; i++) {
			// Overrap判定
			if (i != index) {
				float distance = PApplet.dist(x, y,
						shoppingBasket.iconPositions[i][0],
						shoppingBasket.iconPositions[i][1]);
				if (distance < radius + shoppingBasket.iconPositions[i][2]) {
					returnIndex = i;
					break;
				}
			}
		}
		// if(returnIndex != -1)
		// System.err.println("IndividualIcon:checkOverrapWithOtherIndividual:
		// Index:" + index + " is overrapping with index:" + returnIndex);
		return returnIndex;
	}

	protected boolean checkOnMouse() {
		boolean onMouse = false;
		if (Math.sqrt(Math.pow(Math.abs(x - shoppingBasket.mouseX), 2.0)
				+ Math.pow(Math.abs(y - shoppingBasket.mouseY), 2.0)) < radius) {
			onMouse = true;
			// System.err.println("Mouse on individual:" + index);
		}
		return onMouse;
	}

	protected boolean mousePressed() {
		if (checkOnMouse()) {
			// System.err.println
			// ("Pressed. On icon. Mouse Point is:" + shoppingBasket.mouseX +
			// "," + shoppingBasket.mouseY);
			// onDrag = true;
			return true;
		} else {
			// System.err.println
			// ("Pressed. Not on icon. Mouse Point is:" + shoppingBasket.mouseX
			// + "," + shoppingBasket.mouseY +": index:" + index + ":X,Y:" +x
			// +"," + y);
			// onDrag = false;
			return false;
		}
	}

	protected void mouseReleased() {
		// System.err.println
		// ("Released. Mouse Point is:" + shoppingBasket.mouseX + "," +
		// shoppingBasket.mouseY);
		setDragFlag(false);
		// Detect Current Area
		int newAreaID = shoppingBasket.mouseAreaDetection();
		boolean areaChanged = false;
		if (areaID != newAreaID) {
			changeAreaInformations(newAreaID);
			areaChanged = true;
		}
		// System.err.println("Individual:" + index + " is move to area " +
		// areaID);

		// ApportTargetになっていて引き連れている時の移動でドラッグしてきて放された時
		// 本人はうまい位置に移動する
		// さらに引き連れている個体郡のエリアチェンジ処理と位置調整
		if (areaChanged && onApportCenter) {
			// 本体の位置調整
			// エリアのセンターに位置するようにする
			
			//System.err.print("Current Released icon's containSpace is:");
			//for(int i=0; i<8; i++)
			//System.err.print(apportContainSpace[i] + ",");
			//System.err.println();
			// 引き連れている個体群のエリアチェンジ処理と位置調整
			for (int i = 0; i < 8; i++) {
				int indexOfApportedIcon = apportContainSpace[i];
				if (indexOfApportedIcon != -1) {
					shoppingBasket.iconArray[indexOfApportedIcon]
							.changeAreaInformations(newAreaID);
					shoppingBasket.iconArray[indexOfApportedIcon].setApportTarget(this);
					shoppingBasket.iconArray[indexOfApportedIcon].setApportedFlag(true);
					shoppingBasket.iconArray[indexOfApportedIcon].setApportedPosition();// Position調整
					shoppingBasket.iconArray[indexOfApportedIcon].setProcessedInThisStep(true);// このステップの描画修了
				}
			}
			//for(int i=0; i<shoppingBasket.numOfIndividual; i++)
				//System.err.println("Index:" + shoppingBasket.iconArray[i].index + " 's areaID is: " + shoppingBasket.iconArray[i].getAreaID());
		}
	}

	protected void changeAreaInformations(int newAreaID) {
		areaID = newAreaID;
		int[] areaInformations = shoppingBasket.areaSpace(areaID);
		setArea(areaInformations[0], areaInformations[1], areaInformations[2],
				areaInformations[3], areaID);
	}

	public int getEmptyApportSpace() {
		int counter = 0;
		while (counter < 8) {
			if (apportContainSpace[counter] == -1)
				break;
			counter++;
			if (counter >= 8) {
				counter = -1;
				break;
			}
		}
		return counter;
	}

	public boolean setApportSpace(IndividualIcon icon, int spaceIndex) {
		// 引き寄せている個体のインデックスを格納する
		// falseが返った時は，スペースがいっぱい
		if (apportContainSpace[spaceIndex] == -1) {
			apportContainSpace[spaceIndex] = icon.getIndex();
			// System.err.println("Index:" + index + "'s apport space array
			// is:");
			// for(int i=0;i<shoppingBasket.numOfIndividual; i++)
			// System.err.print(apportContainSpace[i] + " ");
			// System.err.println();
			return true;
		} else {
			return false;
		}
	}

	public void printCurrentInformation(){
		System.err.println("Index:" + index + ", areaID:" + areaID + ", onApported: " + onApported + ", apportTarget:" + apportTarget.index);
	}
	
	public int getContainSpaceIndex(IndividualIcon icon) {
		int returnIndex = -1;
		for (int i = 0; i < 8; i++) {
			if (icon.getIndex() == apportContainSpace[i]) {
				returnIndex = i;
				break;
			}
		}
		return returnIndex;
	}

}
