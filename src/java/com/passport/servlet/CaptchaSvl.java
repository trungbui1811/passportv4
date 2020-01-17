/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.passport.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author TrungBH
 */
public class CaptchaSvl extends HttpServlet{
    private int height = 0;
    private int width = 0;
    public static final String CAPTCHA_KEY = "captcha_key_name";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.height = Integer.parseInt(getServletConfig().getInitParameter("height"));
        this.width = Integer.parseInt(getServletConfig().getInitParameter("width"));
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Max-Age", 0L);
        BufferedImage image = new BufferedImage(this.width, this.height, 1);
        Graphics2D graphics2D = image.createGraphics();
        Random r = new Random();
        Long value = Long.valueOf(r.nextLong());
        if (value.longValue() < 0L)
            value = Long.valueOf(0L - value.longValue()); 
        String token = Long.toString(value.longValue(), 36);
        String ch = token.substring(0, 6);
        Color c = Color.GREEN;
        GradientPaint gp = new GradientPaint(30.0F, 30.0F, c, 15.0F, 25.0F, Color.YELLOW, true);
        graphics2D.setPaint(gp);
        Font font = new Font("Verdana", 1, 26);
        graphics2D.setFont(font);
        graphics2D.drawString(ch, 2, 20);
        graphics2D.dispose();
        HttpSession session = req.getSession(true);
        session.setAttribute("captcha_key_name", ch);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        ImageIO.write(image, "jpeg", (OutputStream)servletOutputStream);
        servletOutputStream.close();
    }
}
