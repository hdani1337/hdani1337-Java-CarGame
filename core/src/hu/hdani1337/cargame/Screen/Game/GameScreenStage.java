package hu.hdani1337.cargame.Screen.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import hu.hdani1337.cargame.CarGame;
import hu.hdani1337.cargame.MyBaseClasses.Assets;
import hu.hdani1337.cargame.MyBaseClasses.Scene2D.MyScreen;
import hu.hdani1337.cargame.MyBaseClasses.Scene2D.MyStage;
import hu.hdani1337.cargame.MyBaseClasses.Scene2D.OneSpriteStaticActor;
import hu.hdani1337.cargame.MyBaseClasses.UI.MyLabel;
import hu.hdani1337.cargame.Screen.Crash.CrashScreenStage;
import hu.hdani1337.cargame.Screen.Options.OptionsScreenStage;
import hu.hdani1337.cargame.Screen.Pause.PauseScreenStage;

public class GameScreenStage extends MyScreen {

	OneSpriteStaticActor myCar;
	OneSpriteStaticActor enemyCar;
	OneSpriteStaticActor block;
	OneSpriteStaticActor background;
	OneSpriteStaticActor background2;
	OneSpriteStaticActor pause;
	OneSpriteStaticActor rightArrow;
	OneSpriteStaticActor leftArrow;

	public static int nehezseg;
	public static int nehezsegNov;
	public static int pontszam;
	public static float myCarDegree;

	Music bgMusic = Assets.manager.get(Assets.GAME_ZENE);
	Sound crash = Assets.manager.get(Assets.CRASH_SOUND);

	MyStage stage = new MyStage(new ExtendViewport(1280,720, new OrthographicCamera(1280, 720)), spriteBatch, game ) {

		int sav;
		int speed;

		@Override
		public void init() {
			speed = nehezseg;

			pause = new OneSpriteStaticActor(Assets.manager.get(Assets.PAUSE_TEXTURE)){
				@Override
					public void setDebug(boolean enabled) {
					super.setDebug(false);
				}
			};
			pause.addListener(new ClickListener(){

				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);
				}

				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					bgMusic.pause();
					game.setScreen(new PauseScreenStage(game));
					return super.touchDown(event, x, y, pointer, button);
				}
			});

			myCar = new OneSpriteStaticActor(Assets.manager.get(Assets.CAR_TEXTURE)){
				@Override
				public void setDebug(boolean enabled) {
					super.setDebug(false);
				}

				@Override
				public void act(float delta){
					super.act(delta);

					//AUTÓ IRÁNYÍTÁS
					if(OptionsScreenStage.controlType == 0){
					setX(getX() +  Gdx.input.getAccelerometerY() * 3);
					setRotation(Gdx.input.getAccelerometerY() * 6);
					}

					if(OptionsScreenStage.controlType == 1){
						leftArrow.addListener(new ClickListener(){
							@Override
							public void clicked(InputEvent event, float x, float y) {
								super.clicked(event, x, y);
								myCar.setX(myCar.getX() - 0.099f);
								myCar.setRotation(-4);
							}
						});

						rightArrow.addListener(new ClickListener(){
							@Override
							public void clicked(InputEvent event, float x, float y) {
								super.clicked(event, x, y);
								myCar.setX(myCar.getX() + 0.099f);
								myCar.setRotation(4);
							}
						});
					}

					if(myCar.getX()<=169 || myCar.getX()>=1070){//Korlátnak ütközés
						bgMusic.stop();
						crash.play();
						myCarDegree = myCar.getRotation();
						pontszam = 0;
						game.setScreen(new CrashScreenStage(game, myCar.getX(),enemyCar.getX(),enemyCar.getY()));
					}
				}
			};//AZ ÉN AUTÓM

			enemyCar = new OneSpriteStaticActor(Assets.manager.get(Assets.ENEMY_TEXTURE)){
				@Override
				public void setDebug(boolean enabled) {
					super.setDebug(false);
				}

				@Override
				public void act(float delta) {
					super.act(delta);
					setY(getY() - delta * speed);
					if(enemyCar.getY() + enemyCar.getHeight() < 0){//Sávválasztás
						sav = (int)(Math.random() * 4 + 1);
						if(sav == 1){
							enemyCar.setX(300);
						}

						if(sav == 2){
							enemyCar.setX(500);
						}

						if(sav == 3){
							enemyCar.setX(735);
						}

						if(sav == 4){
							enemyCar.setX(940);
						}
						setY(stage.getHeight());
						pontszam++;
						speed += nehezsegNov;//Sebességnövelés
						System.out.println(pontszam);
					}

					if(overlaps(myCar,enemyCar) == true){//Ütközés az ellenféllel
						bgMusic.stop();
						crash.play();
						myCarDegree = myCar.getRotation();
						pontszam = 0;
						game.setScreen(new CrashScreenStage(game, myCar.getX(), enemyCar.getX(),enemyCar.getY()));
					}
				}
			};//ELLENSÉGES AUTÓ

			background = new OneSpriteStaticActor(Assets.manager.get(Assets.HATTER_TEXTURE)){
				@Override
				public void setDebug(boolean enabled) {
					super.setDebug(false);
				}

				@Override
				public void act(float delta) {
					super.act(delta);
					background.setY(getY() - 5);

					if(OptionsScreenStage.ifMuted == 0){
						bgMusic.setLooping(true);
						bgMusic.setVolume(0.4f);
						bgMusic.play();
					}

					if(OptionsScreenStage.ifMuted == 1){
						bgMusic.stop();
					}

					if(background.getY() + 720 <= 0){
						background.setY(720);
					}
				}
			};//HÁTTÉR

			background2 = new OneSpriteStaticActor(Assets.manager.get(Assets.HATTER_TEXTURE)){
				@Override
				public void setDebug(boolean enabled) {
					super.setDebug(false);
				}

				@Override
				public void act(float delta) {
					super.act(delta);
					background2.setY(getY() - 5);

					if(background2.getY() + 720 <= 0){
						background2.setY(720);
					}
				}
			};//HÁTTÉR

			block = new OneSpriteStaticActor(Assets.manager.get(Assets.BLOCK_TEXTURE)){
				@Override
				public void setDebug(boolean enabled) {
				super.setDebug(false);
				}

				@Override
				public void act(float delta) {
					super.act(delta);
					setY(getY() - 7);
					if(block.getY() + block.getHeight() < 0){//Sávválasztás
						sav = (int)(Math.random() * 4 + 1);
						if(sav == 1){
							block.setX(250);
						}

						if(sav == 2){
							block.setX(450);
						}

						if(sav == 3){
							block.setX(685);
						}

						if(sav == 4){
							block.setX(890);
						}
						setY((int)(Math.random() * 5000 + 1000));
						pontszam++;
						speed += nehezsegNov;//Sebességnövelés
					}

					if(overlaps(myCar,block) == true){//Ütközés az ellenféllel
						bgMusic.stop();
						crash.play();
						myCarDegree = myCar.getRotation();
						pontszam = 0;
						game.setScreen(new CrashScreenStage(game, myCar.getX(), enemyCar.getX(),enemyCar.getY()));
					}
				}

			};//AKADÁLY

			leftArrow = new OneSpriteStaticActor(Assets.manager.get(Assets.LEFT_ARROW)){
				@Override
				public void setDebug(boolean enabled) {
					super.setDebug(false);
				}
			};//BALRA GOMB
			rightArrow = new OneSpriteStaticActor(Assets.manager.get(Assets.RIGHT_ARROW)){
				@Override
				public void setDebug(boolean enabled) {
					super.setDebug(false);
				}
			};//JOBBRA GOMB


			myCar.setSize(50,120);
			myCar.addBaseCollisionRectangleShape();
			myCar.setPosition(615,5);

			enemyCar.addBaseCollisionRectangleShape();
			enemyCar.setSize(50,95);
			enemyCar.setPosition(500,getHeight());

			background.setSize(1280,720);
			background.setPosition(0,0);

			background2.setSize(1280,720);
			background2.setPosition(0,720);

			pause.setPosition(1205,645);
			pause.setSize(72,72);

			block.setPosition(450,5000);
			block.setSize(150,150);

			addActor(background);
			addActor(background2);
			addActor(myCar);
			addActor(enemyCar);
			addActor(block);
			addActor(pause);

			if(OptionsScreenStage.controlType == 1){
				leftArrow.setSize(75,75);
				rightArrow.setSize(75,75);

				leftArrow.setPosition(950,50);
				rightArrow.setPosition(1050,50);
				addActor(leftArrow);
				addActor(rightArrow);
			}
		}

	};

	public GameScreenStage(CarGame game) {
		super(game);
	}


	@Override
	public void init() {

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			myCar.setX(myCar.getX() - 6);
			myCar.setRotation(-5);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			myCar.setX(myCar.getX() + 6);
			myCar.setRotation(5);
		}
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}