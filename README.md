# Build Swords How **you** imagine them

### About
This mod is a mod for building custom Swords and Lightsabers out of different parts you can craft.
</br>
You can also add your own Models, Texture and Animations.

---

### Table of Contents
1. [How to use](#how-to-use)
2. [Dependencies](#requirments)
3. [Why?](#why)

### How to use:
The following command should explain everything there is to know, colors are in the hex format and blades can have an optional model. If a model is provided it will not render a lightsaber blade.
Specifying a color when using a blade model is currently unimplemented.

```jsonc
/give @p partium:sword[
	partium:parts = {
	"blades":[
		{"primary":
			{ "length":1.0, "innerColor": "#ffffff", "outercolor": "#44aaff", 
			  "model":"" }
		},
		{"secondary":
			{ "length":0.5, "innerColor": "#ffffff", "outercolor": "#44aaff",
			  "model":"" }
		},
		{"tertiary":
			{ "length":0.25, "innerColor": "#ffffff", "outercolor": "#44aaff",
			  "model":"" }
		},
	],
	"emitter":{
		"model": "addon_pack:my_cool_emitter", "scale": 1.0
	},
	"guard":{
		"model": "addon_pack:my_cool_guard", "scale": 1.1
	},
	"grip":{
		"model": "addon_pack:my_cool_grip", "scale": 1.0
	},
	"pommel":{
		"model": "addon_pack:my_cool_pommel", "scale": 0.3
	}
}]


//Test components contained in the mod
/give @p partium:sword[partium:parts={
"grip":{"model":"partium:test_grip"},
"pommel":{"model":"partium:test_pommel", "scale":0.75f},
"emitter":{"model":"partium:test_emitter", "scale":0.75f},
"guard":{"model":"partium:test_guard", "scale":0.75f}
}]
```

#### Requirments:
 - [Geckolib](https://modrinth.com/mod/geckolib)

</br>
</br>

### Why?
Don't you miss your childhood memories, you made with the [Fisk's Advanced Lighsabers Mod](https://www.curseforge.com/minecraft/mc-mods/advanced-lightsabers) and want to bring them back? I do and so I decided to make this mod. It utilizes the power of [Geckolib](https://modrinth.com/mod/geckolib) to create cool looking Swords and Lightsabers.
