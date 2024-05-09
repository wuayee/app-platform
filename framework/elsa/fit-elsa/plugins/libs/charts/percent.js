let percent = (id, x, y, width, height, parent) => {
    const DEFAULT = 15;
    let self = vector(id, x, y, width, height, parent);
    self.type = "percent";
    self.namespace = "chart";
    self.borderWidth = 0;
    self.text = "";
    self.fontSize = 23;
    self.editable = false;
    self.value = DEFAULT;
    self.qulifiedValue = 60;
    self.goodValue = 80;
    self.direction = 0;//0:越大越好；1越小越好
    self.margin = self.pad = 0;

    let x0, y0, h, r;
    self.drawStatic = (context, x, y, width, height) => {
        const steps = 30;
        x0 = x + width / 2;
        y0 = y + height / 2 + (self.text === "" ? 0 : self.fontSize);
        h = height - (self.text === "" ? 0 : self.fontSize);
        r = (h < width ? h / 2 : width / 2) - 5;
        if (isNaN(self.value)) {
            self.value = 0;
        }
        if (self.value > 100) {
            self.value = 100;
        }
        if (self.value < 0) {
            self.value = 0;
        }

        context.lineWidth = 5;
        let step = 2 * Math.PI / steps;
        for (let i = 0; i < steps; i++) {
            context.strokeStyle = i * 100 / steps < self.value ? self.borderColor : "silver";
            context.beginPath();
            context.arc(x0, y0, r, i * step, (i + 0.5) * step);
            context.stroke();
        }

        context.lineWidth = 1;
        context.strokeStyle = "whitesmoke";
        context.beginPath();
        context.arc(x0, y0, r - 8, 0, 2 * Math.PI);
        context.stroke();

        let text = self.value + "%";
        context.font = "normal bold " + self.fontSize + "px Arial";
        let w = context.measureText(text).width;
        context.fillStyle = self.fontColor;
        context.fillText(text, x0 - w / 2, y0 + self.fontSize / 2 - 3);
    };

    let degree = 0, flash = 0;
    self.drawDynamic = (context, width, height) => {
        let tranformedValue = self.direction === 0 ? self.value : (100 - selfvalue);
        let color = "pink";
        if (tranformedValue > self.qulifiedValue) {
            color = "gold";
        }
        if (tranformedValue > self.goodValue) {
            color = "lightgreen";
        }

        context.save();
        context.translate(x0, y0);
        context.rotate(degree);
        if (tranformedValue < self.qulifiedValue && ((flash >= 100 && flash < 110) || (flash >= 130 && flash < 140))) {
            context.beginPath();
            context.arc(0, 0, r - 8, 0, 2 * Math.PI);
            context.fillStyle = "RGB(255,0,0,0.3)";
            context.fill();
        }

        let g = context.createRadialGradient(0, 0, 1, 0, 0, r - 8);
        g.addColorStop(0, "rgba(255,255,255,0)");
        g.addColorStop(1, color);
        context.fillStyle = g;
        context.beginPath();
        context.arc(0, 0, r - 8, 0, 0.4 * Math.PI);
        context.lineTo(0, 0);
        context.closePath();
        context.fill();
        context.restore();
        degree += 0.01;
        flash++;
        if (degree > 360) {
            degree = 0;
        }
        if (flash > 200) {
            flash = 0;
        }
    };

    // self.serializedFields.batchAdd("value");
    // self.serializedFields.batchAdd("qulifiedValue");
    // self.serializedFields.batchAdd("goodValue");
    // self.serializedFields.batchAdd("direction");

    return self;
};