/**
 * This file is part of Aion X Emu <aionxemu.com>
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package gameserver.model.gameobjects.player;

/**
 * @author SoulKeeper, srx47, alexa026, Divinity
 */

public class PlayerAppearance implements Cloneable {

    /**
     * Player's face
     */
    private int    voice;
    private int    skinRGB;
    private int    hairRGB;
    private int    lipRGB;
    private int    eyeRGB;
    private int    face;
    private int    hair;
    private int    decoration;
    private int    tattoo;
    private int    faceContour;
    private int    expression;
    private int    jawLine;
    private int    forehead;
    private int    eyeHeight;
    private int    eyeSpace;
    private int    eyeWidth;
    private int    eyeSize;
    private int    eyeShape;
    private int    eyeAngle;
    private int    browHeight;
    private int    browAngle;
    private int    browShape;
    private int    nose;
    private int    noseBridge;
    private int    noseWidth;
    private int    noseTip;
    private int    cheeks;
    private int    lipHeight;
    private int    mouthSize;
    private int    lipSize;
    private int    smile;
    private int    lipShape;
    private int    chinHeight;
    private int    cheekBones;
    private int    earShape;
    private int    headSize;
    private int    neck;
    private int    neckLength;
    private int    shoulderSize;
    private int    torso;
    private int    chest;
    private int    waist;
    private int    hips;
    private int    armThickness;
    private int    handSize;
    private int    legThickness;
    private int    footSize;
    private int    facialRatio;
    private int    armLength;
    private int    legLength;
    private int    shoulders;
    private int    faceShape;
    private float    height;


    /**
     * Returns sexy voice
     * 
     * @return sexy voice
     */
    public int getVoice()
    {
        return voice;
    }

    /**
     * Sets sexy voice
     * 
     * @param voice
     *            sexy voice
     */
    public void setVoice(int voice)
    {
        this.voice = voice;
    }

    /**
     * Skin color, let's create pink lesbians :D
     * 
     * @return skin color
     */
    public int getSkinRGB()
    {
        return skinRGB;
    }

    /**
     * Here is the valid place to make lesbians skin pink
     * 
     * @param skinRGB
     *            skin color
     */
    public void setSkinRGB(int skinRGB)
    {
        this.skinRGB = skinRGB;
    }

    /**
     * Hair color, personally I prefer brunettes
     * 
     * @return hair color
     */
    public int getHairRGB()
    {
        return hairRGB;
    }

    /**
     * Sets hair colors. Blonds must pass IQ test ;)
     * 
     * @param hairRGB
     *            hair color
     */
    public void setHairRGB(int hairRGB)
    {
        this.hairRGB = hairRGB;
    }
    /**
     * Eyes color
     *
     * @return eyes color
     */
    public void setEyeRGB(int eyeRGB)
    {
        this.eyeRGB = eyeRGB;
    }
    
    /**
     * Sets eyes color
     *
     * @param eyeRGB
     *            eyes color
     */
    public int getEyeRGB()
    {
    return eyeRGB;
    }

    /**
     * Lips color
     * 
     * @return lips color
     */
    public int getLipRGB()
    {
        return lipRGB;
    }

    /**
     * Sets lips color
     * 
     * @param lipRGB
     *            lips color
     */
    public void setLipRGB(int lipRGB)
    {
        this.lipRGB = lipRGB;
    }

    /**
     * Returns character face
     * 
     * @return character face
     */
    public int getFace()
    {
        return face;
    }

    /**
     * Sets character's face
     * 
     * @param face
     *            character's face id
     */
    public void setFace(int face)
    {
        this.face = face;
    }

    /**
     * Returns character's hair
     * 
     * @return character's hair
     */
    public int getHair()
    {
        return hair;
    }

    /**
     * Sets character's hair
     * 
     * @param hair
     *            character's hair id
     */
    public void setHair(int hair)
    {
        this.hair = hair;
    }

    /**
     * Returns decoration 
     * Blush, freckles, scars etc
     * 
     * @return decoration
     */
    public int getDecoration()
    {
        return decoration;
    }

    /**
     * Sets decoration
     * 
     * @param decoration
     *            decoration id
     */
    public void setDecoration(int decoration)
    {
        this.decoration = decoration;
    }

    /**
     * Returns sexy tattoo
     * 
     * @return sexy tattoo
     */
    public int getTattoo()
    {
        return tattoo;
    }

    /**
     * Set's sexy tattoo.
     * Not sexy will throw NotSexyTattooException. Just kidding ;)
     * 
     * @param tattoo
     *            some tattoo
     */
    public void setTattoo(int tattoo)
    {
        this.tattoo = tattoo;
    }

    /**
     * Returns face contour
     * 2.5 parameter
     * 
     * @return face contour
     */
    public int getFaceContour()
    {
        return faceContour;
    }

    /**
     * Sets face contour
     * 2.5 parameter
     * 
     * @param faceContour
     *            face contour
     */
    public void setFaceContour(int faceContour)
    {
        this.faceContour = faceContour;
    }

    /**
     * Returns expression
     * 2.5 parameter
     * 
     * @return expression
     */
    public int getExpression()
    {
        return expression;
    }

    /**
     * Sets expression
     * 2.5 parameter
     * 
     * @param expression
     *            expression number (1-6)
     */
    public void setExpression(int expression)
    {
        this.expression = expression;
    }

    /**
     * Returns jaw line
     * Was jaw line until 2.5
     * 
     * @return jaw line
     */
    public int getJawLine()
    {
        return jawLine;
    }

    /**
     * Sets jaw line
     * Was face shape until 2.5
     * 
     * @param jawLine
     *            jaw line
     */
    public void setJawLine(int jawLine)
    {
        this.jawLine = jawLine;
    }

    /**
     * Returns forehead
     * 
     * @return forehead
     */
    public int getForehead()
    {
        return forehead;
    }

    /**
     * Sets forehead
     * 
     * @param forehead
     *            size
     */
    public void setForehead(int forehead)
    {
        this.forehead = forehead;
    }

    /**
     * Returns eyes height
     * 
     * @return eyes height
     */
    public int getEyeHeight()
    {
        return eyeHeight;
    }

    /**
     * Sets eyes height
     * 
     * @param eyeHeight
     *            eyes height
     */
    public void setEyeHeight(int eyeHeight)
    {
        this.eyeHeight = eyeHeight;
    }

    /**
     * Eyes space
     * 
     * @return eyes space
     */
    public int getEyeSpace()
    {
        return eyeSpace;
    }

    /**
     * Eyes space
     * 
     * @param eyeSpace
     *            space between eyes
     */
    public void setEyeSpace(int eyeSpace)
    {
        this.eyeSpace = eyeSpace;
    }

    /**
     * Returns eyes width
     * 
     * @return eyes width
     */
    public int getEyeWidth()
    {
        return eyeWidth;
    }

    /**
     * Sets eyes width
     * 
     * @param eyeWidth
     *            eyes width
     */
    public void setEyeWidth(int eyeWidth)
    {
        this.eyeWidth = eyeWidth;
    }

    /**
     * Returns eyes size. Hentai girls usually have very big eyes
     * 
     * @return eyes
     */
    public int getEyeSize()
    {
        return eyeSize;
    }

    /**
     * Set's eye size.<br>
     * Can be . o O ;)
     * 
     * @param eyeSize
     *            eyes size
     */
    public void setEyeSize(int eyeSize)
    {
        this.eyeSize = eyeSize;
    }

    /**
     * Return eyes shape
     * 
     * @return eyes shape
     */
    public int getEyeShape()
    {
        return eyeShape;
    }

    /**
     * Sets eyes shape.
     * Can be . _ | 0 o O etc :)
     * 
     * @param eyeShape
     *            eyes shape
     */
    public void setEyeShape(int eyeShape)
    {
        this.eyeShape = eyeShape;
    }

    /**
     * Return eyes angle
     * 
     * @return eyes angle
     */
    public int getEyeAngle()
    {
        return eyeAngle;
    }

    /**
     * Sets eyes angle, / | \.
     * 
     * @param eyeAngle
     *            eyes angle
     */
    public void setEyeAngle(int eyeAngle)
    {
        this.eyeAngle = eyeAngle;
    }

    /**
     * Rerturn brow height
     * 
     * @return brow height
     */
    public int getBrowHeight()
    {
        return browHeight;
    }

    /**
     * Brow height
     * 
     * @param browHeight
     *            brow height
     */
    public void setBrowHeight(int browHeight)
    {
        this.browHeight = browHeight;
    }

    /**
     * Returns brow angle
     * 
     * @return brow angle
     */
    public int getBrowAngle()
    {
        return browAngle;
    }

    /**
     * Sets brow angle
     * 
     * @param browAngle
     *            brow angle
     */
    public void setBrowAngle(int browAngle)
    {
        this.browAngle = browAngle;
    }

    /**
     * Returns brow shape
     * 
     * @return brow shape
     */
    public int getBrowShape()
    {
        return browShape;
    }

    /**
     * Sets brow shape
     * 
     * @param browShape
     *            brow shape
     */
    public void setBrowShape(int browShape)
    {
        this.browShape = browShape;
    }

    /**
     * Returns nose
     * 
     * @return nose
     */
    public int getNose()
    {
        return nose;
    }

    /**
     * Sets nose
     * 
     * @param nose
     *            nose
     */
    public void setNose(int nose)
    {
        this.nose = nose;
    }

    /**
     * Returns nose bridge
     * 
     * @return nose bridge
     */
    public int getNoseBridge()
    {
        return noseBridge;
    }

    /**
     * Sets nose bridge
     * 
     * @param noseBridge
     *            nose bridge
     */
    public void setNoseBridge(int noseBridge)
    {
        this.noseBridge = noseBridge;
    }

    /**
     * Returns nose width
     * 
     * @return nose width
     */
    public int getNoseWidth()
    {
        return noseWidth;
    }

    /**
     * Sets nose width
     * 
     * @param noseWidth
     *            nose width
     */
    public void setNoseWidth(int noseWidth)
    {
        this.noseWidth = noseWidth;
    }

    /**
     * Returns nose tip
     * 
     * @return nose tip
     */
    public int getNoseTip()
    {
        return noseTip;
    }

    /**
     * Sets nose tip
     * 
     * @param noseTip
     *            nose tip
     */
    public void setNoseTip(int noseTip)
    {
        this.noseTip = noseTip;
    }

    /**
     * Returns cheeks
     * 
     * @return cheeks
     */
    public int getCheeks()
    {
        return cheeks;
    }

    /**
     * Sets cheeks
     * 
     * @param cheeks
     *            cheeks
     */
    public void setCheeks(int cheeks)
    {
        this.cheeks = cheeks;
    }

    /**
     * Returns lips height
     * 
     * @return lips height
     */
    public int getLipHeight()
    {
        return lipHeight;
    }

    /**
     * Sets lips height
     * 
     * @param lipHeight
     *            lips height
     */
    public void setLipHeight(int lipHeight)
    {
        this.lipHeight = lipHeight;
    }

    /**
     * Returns mouth size
     * 
     * @return mouth size
     */
    public int getMouthSize()
    {
        return mouthSize;
    }

    /**
     * Sets mouth size
     * 
     * @param mouthSize
     *            mouth size
     */
    public void setMouthSize(int mouthSize)
    {
        this.mouthSize = mouthSize;
    }

    /**
     * Returns lips size
     * 
     * @return lips size
     */
    public int getLipSize()
    {
        return lipSize;
    }

    /**
     * Sets lips size
     * 
     * @param lipSize
     *            lips size
     */
    public void setLipSize(int lipSize)
    {
        this.lipSize = lipSize;
    }

    /**
     * Returns smile
     * 
     * @return smile
     */
    public int getSmile()
    {
        return smile;
    }

    /**
     * Sets smile
     * 
     * @param smile
     *            smile
     */
    public void setSmile(int smile)
    {
        this.smile = smile;
    }

    /**
     * Returns lips shape
     * 
     * @return lips shape
     */
    public int getLipShape()
    {
        return lipShape;
    }

    /**
     * Sets lips shape
     * 
     * @param lipShape
     *            lips shape
     */
    public void setLipShape(int lipShape)
    {
        this.lipShape = lipShape;
    }

    /**
     * Returns chin height
     * Was jaw height until 2.5
     * 
     * @return chin height
     */
    public int getChinHeight()
    {
        return chinHeight;
    }

    /**
     * Sets chin height
     * Was jaw height until 2.5
     * 
     * @param chinHeight
     *            chin height
     */
    public void setChinHeight(int chinHeight)
    {
        this.chinHeight = chinHeight;
    }

    /**
     * Returns cheek bones
     * Was chin jut until 2.5
     * 
     * @return cheek bones
     */
    public int getCheekBones()
    {
        return cheekBones;
    }

    /**
     * Sets cheek bones
     * Was chin jut until 2.5
     * 
     * @param cheek bones
     *            cheek bones
     */
    public void setCheekBones(int cheekBones)
    {
        this.cheekBones = cheekBones;
    }

    /**
     * Returns ears shape
     * 
     * @return ears shape
     */
    public int getEarShape()
    {
        return earShape;
    }

    /**
     * Sets ears shape
     * 
     * @param earShape
     *            ears shape
     */
    public void setEarShape(int earShape)
    {
        this.earShape = earShape;
    }

    /**
     * Returns head size
     * 
     * @return head size
     */
    public int getHeadSize()
    {
        return headSize;
    }

    /**
     * Sets head size
     * 
     * @param headSize
     *            head size
     */
    public void setHeadSize(int headSize)
    {
        this.headSize = headSize;
    }

    /**
     * Returns neck
     * 
     * @return neck
     */
    public int getNeck()
    {
        return neck;
    }

    /**
     * Sets neck
     * 
     * @param neck
     *            neck
     */
    public void setNeck(int neck)
    {
        this.neck = neck;
    }

    /**
     * Returns neck length
     * 
     * @return neck length
     */
    public int getNeckLength()
    {
        return neckLength;
    }

    /**
     * Sets neck length, just curious, is it possible to create a giraffe?
     * 
     * @param neckLength
     *            neck length
     */
    public void setNeckLength(int neckLength)
    {
        this.neckLength = neckLength;
    }

    /**
     * Returns shoulders size
     * 
     * @return shoulderSize
     */
    public int getShoulderSize()
    {
        return shoulderSize;
    }

    /**
     * Sets shoulders size
     * 
     * @param shoulderSize
     *            shoulderSize
     */
    public void setShoulderSize(int shoulderSize)
    {
        this.shoulderSize = shoulderSize;
    }


    /**
     * Returns torso
     * 
     * @return torso
     */
    public int getTorso()
    {
        return torso;
    }

    /**
     * Sets torso
     * 
     * @param torso
     *            torso
     */
    public void setTorso(int torso)
    {
        this.torso = torso;
    }

    /**
     * Returns tits
     * 
     * @return tits
     */
    public int getChest()
    {
        return chest;
    }

    /**
     * Sets tits
     * 
     * @param chest
     *            tits
     */
    public void setChest(int chest)
    {
        this.chest = chest;
    }

    /**
     * Returns waist
     * 
     * @return waist
     */
    public int getWaist()
    {
        return waist;
    }

    /**
     * sets waist
     * 
     * @param waist
     *            waist
     */
    public void setWaist(int waist)
    {
        this.waist = waist;
    }

    /**
     * Returns hips
     * 
     * @return hips
     */
    public int getHips()
    {
        return hips;
    }

    /**
     * Sets hips
     * 
     * @param hips
     *            hips
     */
    public void setHips(int hips)
    {
        this.hips = hips;
    }

    /**
     * Returns arm thickness
     * 
     * @return arm thickness
     */
    public int getArmThickness()
    {
        return armThickness;
    }

    /**
     * Sets arm thickness
     * 
     * @param armThickness
     *            arm thickness
     */
    public void setArmThickness(int armThickness)
    {
        this.armThickness = armThickness;
    }

    /**
     * Returns hands size
     * 
     * @return hands size
     */
    public int getHandSize()
    {
        return handSize;
    }

    /**
     * Sets hands size
     * 
     * @param handSize
     *            hands size
     */
    public void setHandSize(int handSize)
    {
        this.handSize = handSize;
    }

    /**
     * Returns legs thickness
     * 
     * @return legs thickness
     */
    public int getLegThickness()
    {
        return legThickness;
    }

    /**
     * Sets legs thickness
     * 
     * @param legThicnkess
     *            legs thickness
     */
    public void setLegThickness(int legThickness)
    {
        this.legThickness = legThickness;
    }


    /**
     * Returns feet size
     * 
     * @return foot size
     */
    public int getFootSize()
    {
        return footSize;
    }

    /**
     * Sets feet size
     * 
     * @param footSize
     *            feet size
     */
    public void setFootSize(int footSize)
    {
        this.footSize = footSize;
    }

    /**
     * Retunrs facial ratio
     * 
     * @return facial ratio
     */
    public int getFacialRatio()
    {
        return facialRatio;
    }

    /**
     * Sets facial ratio
     * 
     * @param facialRatio
     *            facial ratio
     */
    public void setFacialRatio(int facialRatio)
    {
        this.facialRatio = facialRatio;
    }

    /**
     * Returns arms length
     * 
     * @return arms length
     */
    public int getArmLength()
    {
        return armLength;
    }

    /**
     * Sets arms length
     * 
     * @param armLength
     *            arms length
     */
    public void setArmLength(int armLength)
    {
        this.armLength = armLength;
    }

    /**
     * Returns legs length
     * 
     * @return legs length
     */
    public int getLegLength()
    {
        return legLength;
    }

    /**
     * Sets legs length
     * 
     * @param legLength
     *            legs length
     */
    public void setLegLength(int legLength)
    {
        this.legLength = legLength;
    }

    /**
     * Return shoulders
     * 
     * @return shoulders
     */
    public int getShoulders()
    {
        return shoulders;
    }

    /**
     * Set shoulders
     * 
     * @param shoulders
     *            shoulders
     */
    public void setShoulders(int shoulders)
    {
        this.shoulders = shoulders;
    }

    /**
     * Returns face shape
     * 2.5 parameter, do not confuse with pre-2.5 face shape
     * 
     * @return face shape
     */
    public int getFaceShape()
    {
        return faceShape;
    }

    /**
     * Sets face shape
     * 2.5 parameter, do not confuse with pre-2.5 face shape
     * 
     * @param faceShape
     *            face shape
     */
    public void setFaceShape(int faceShape)
    {
        this.faceShape = faceShape;
    }

    /**
     * Returns height
     * 
     * @return height
     */
    public float getHeight()
    {
        return height;
    }

    /**
     * Sets height
     * 
     * @param height
     *            height
     */
    public void setHeight(float height)
    {
        this.height = height;
    }
    
    public Object clone()
    {
        Object newObject = null;
        
        try
        {
            newObject = super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        
        return newObject;
    }
}
