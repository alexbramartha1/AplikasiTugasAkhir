package com.reviling.filamentandroid.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.data.preferences.StatePlayer
import com.reviling.filamentandroid.data.response.AudioArrayGamelanItem
import com.reviling.filamentandroid.data.response.AudioArrayItem
import com.reviling.filamentandroid.data.response.AudioGamelanItem
import com.reviling.filamentandroid.data.response.AudioInstrumenItem
import com.reviling.filamentandroid.data.response.DataUserItem
import com.reviling.filamentandroid.data.response.GamelanDataItem
import com.reviling.filamentandroid.data.response.InstrumentDataItem
import com.reviling.filamentandroid.data.response.SanggarDataItem
import com.reviling.filamentandroid.databinding.AudioListCardBinding
import com.reviling.filamentandroid.databinding.AudioListInputBinding
import com.reviling.filamentandroid.databinding.GamelanBaliCardBinding
import com.reviling.filamentandroid.databinding.InstrumenBaliCardBinding
import com.reviling.filamentandroid.databinding.JenisUpacaraCardBinding
import com.reviling.filamentandroid.databinding.MaterialListCardBinding
import com.reviling.filamentandroid.databinding.MaterialListCardInputBinding
import com.reviling.filamentandroid.databinding.SanggarBaliCardBinding
import com.reviling.filamentandroid.databinding.SanggarSeeAllCardBinding
import com.reviling.filamentandroid.databinding.SeeAllGamelanBinding
import com.reviling.filamentandroid.databinding.UsersCardBinding
import com.reviling.filamentandroid.ui.detailgamelan.DetailGamelanActivity
import com.reviling.filamentandroid.ui.detailinstrument.DetailInstrumentActivity
import com.reviling.filamentandroid.ui.detailsanggar.DetailSanggarActivity
import java.io.IOException

class HomeAdapter(private val context: Context): RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = GamelanBaliCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listGamelan[position])
        val gamelan = listGamelan[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailGamelanActivity::class.java)
            intent.putExtra(DetailGamelanActivity.ID, gamelan.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listGamelan.size
    }

    private val listGamelan = ArrayList<GamelanDataItem>()

    fun setListGamelan(listGamelanData: List<GamelanDataItem>) {
        val diffCallback = GamelanBaliDiffCallback(this.listGamelan, listGamelanData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listGamelan.clear()
        this.listGamelan.addAll(listGamelanData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: GamelanBaliCardBinding, val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(gamelan: GamelanDataItem) {
            binding.gamelanName.text = gamelan.namaGamelan
            binding.gamelanGolongan.text = context.getString(R.string.gol_value, gamelan.golongan)
            binding.gamelanDesc.text = gamelan.description
        }
    }
}

class GamelanAdapter(private val context: Context): RecyclerView.Adapter<GamelanAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SeeAllGamelanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listGamelan[position])
        val gamelan = listGamelan[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailGamelanActivity::class.java)
            intent.putExtra(DetailGamelanActivity.ID, gamelan.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listGamelan.size
    }

    private val listGamelan = ArrayList<GamelanDataItem>()

    fun setListGamelan(listGamelanData: List<GamelanDataItem>) {
        val diffCallback = GamelanBaliDiffCallback(this.listGamelan, listGamelanData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listGamelan.clear()
        this.listGamelan.addAll(listGamelanData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: SeeAllGamelanBinding, val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(gamelan: GamelanDataItem) {
            binding.gamelanName.text = gamelan.namaGamelan
            binding.gamelanGolongan.text = context.getString(R.string.gol_value, gamelan.golongan)
            binding.gamelanDesc.text = gamelan.description
        }
    }
}

class InstrumentAdapter(private val context: Context): RecyclerView.Adapter<InstrumentAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = InstrumenBaliCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listInstrument[position])

        val instrument = listInstrument[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailInstrumentActivity::class.java)
            intent.putExtra(DetailInstrumentActivity.ID, instrument.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listInstrument.size
    }

    private val listInstrument = ArrayList<InstrumentDataItem>()

    fun setListInstrument(listInstrumentData: List<InstrumentDataItem>) {
        val diffCallback = InstrumentBaliDiffCallback(this.listInstrument, listInstrumentData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listInstrument.clear()
        this.listInstrument.addAll(listInstrumentData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: InstrumenBaliCardBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(instrument: InstrumentDataItem) {
            Glide.with(binding.root)
                .load(instrument.imageInstrumen[0])
                .transform(CenterCrop(), RoundedCorners(20))
                .into(binding.instrumenImage)

            val result = instrument.bahan.joinToString(", ")
            binding.materialId.text = result

            binding.instrumenName.text = instrument.namaInstrument

        }
    }
}

class SanggarAdapter(private val context: Context): RecyclerView.Adapter<SanggarAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SanggarBaliCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listSanggar[position])

        val sanggar = listSanggar[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailSanggarActivity::class.java)
            intent.putExtra(DetailSanggarActivity.ID, sanggar.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listSanggar.size
    }

    private val listSanggar = ArrayList<SanggarDataItem>()

    fun setListSanggar(listSanggarData: List<SanggarDataItem>) {
        val diffCallback = SanggarBaliDiffCallback(this.listSanggar, listSanggarData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listSanggar.clear()
        this.listSanggar.addAll(listSanggarData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: SanggarBaliCardBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(sanggar: SanggarDataItem) {
            Glide.with(binding.root)
                .load(sanggar.image)
                .transform(CenterCrop(), RoundedCorners(20))
                .into(binding.sanggarImage)

            binding.sanggarName.text = sanggar.namaSanggar
            binding.sanggarNumber.text = sanggar.noTelepon
            binding.city.text = sanggar.kabupaten
        }
    }
}

class SanggarSeeAllAdapter(private val context: Context): RecyclerView.Adapter<SanggarSeeAllAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SanggarSeeAllCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listSanggar[position])

        val sanggar = listSanggar[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailSanggarActivity::class.java)
            intent.putExtra(DetailSanggarActivity.ID, sanggar.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listSanggar.size
    }

    private val listSanggar = ArrayList<SanggarDataItem>()

    fun setListSanggarAll(listSanggarData: List<SanggarDataItem>) {
        val diffCallback = SanggarBaliDiffCallback(this.listSanggar, listSanggarData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listSanggar.clear()
        this.listSanggar.addAll(listSanggarData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: SanggarSeeAllCardBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {

        fun bind(sanggar: SanggarDataItem) {
            Glide.with(binding.root)
                .load(sanggar.image)
                .transform(CenterCrop(), RoundedCorners(20))
                .into(binding.sanggarImage)

            binding.sanggarName.text = sanggar.namaSanggar
            binding.sanggarNumber.text = sanggar.noTelepon
            binding.city.text = sanggar.kabupaten
        }
    }
}

class UpacaraAdapter(private val context: Context): RecyclerView.Adapter<UpacaraAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = JenisUpacaraCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listUpacara[position])
    }

    override fun getItemCount(): Int {
        return listUpacara.size
    }

    private val listUpacara = ArrayList<String>()

    fun setListUpacara(listUpacaraData: List<String>) {
        val diffCallback = UpacaraDiffCallback(this.listUpacara, listUpacaraData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listUpacara.clear()
        this.listUpacara.addAll(listUpacaraData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: JenisUpacaraCardBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(upacara: String) {
            binding.upacaralist.text = upacara
        }
    }
}

class AudioAdapter(private val context: Context): RecyclerView.Adapter<AudioAdapter.MyViewHolder>() {
    private lateinit var statePlayer: StatePlayer
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private var currentPlayingPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AudioListCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        statePlayer = StatePlayer(false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listAudio[position])
        val audio = listAudio[position]

        // Reflect playback state in the UI
        if (position == currentPlayingPosition && statePlayer.playingAudio) {
            holder.binding.playView.visibility = View.GONE
            holder.binding.pauseView.visibility = View.VISIBLE
            holder.binding.deskripsiTitle.visibility = View.VISIBLE
            holder.binding.audioDescFull.visibility = View.VISIBLE
            holder.binding.root.requestLayout()
        } else {
            holder.binding.playView.visibility = View.VISIBLE
            holder.binding.pauseView.visibility = View.GONE
            holder.binding.deskripsiTitle.visibility = View.GONE
            holder.binding.audioDescFull.visibility = View.GONE
            holder.binding.root.requestLayout()
        }

        holder.binding.button.setOnClickListener {
            playAudio(audio.audioPath, position)
            holder.binding.root.requestLayout()
        }

    }

    override fun getItemCount(): Int {
        return listAudio.size
    }

    private val listAudio = ArrayList<AudioGamelanItem>()

    fun setListAudio(listAudioData: List<AudioGamelanItem>) {
        val diffCallback = AudioBaliDiffCallback(this.listAudio, listAudioData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listAudio.clear()
        this.listAudio.addAll(listAudioData)
        diffResult.dispatchUpdatesTo(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun stopAudioPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
            currentPlayingPosition = RecyclerView.NO_POSITION
            notifyDataSetChanged() // Update UI to reflect stopped state
        }
    }

    private fun playAudio(audioUrl: String, position: Int) {

        if (mediaPlayer == null || currentPlayingPosition != position) {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null

                // Perbarui UI item sebelumnya
                val previousPosition = currentPlayingPosition
                currentPlayingPosition = position
                notifyItemChanged(previousPosition) // Update UI item sebelumnya
            }

            currentPlayingPosition = position

            // Initialize the MediaPlayer for the first time
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    this@AudioAdapter.isPlaying = true
                    statePlayer.playingAudio = true
                    notifyItemChanged(position)
                    start()
                }
                setOnCompletionListener {
                    this@AudioAdapter.isPlaying = false
                    statePlayer.playingAudio = false
                    notifyItemChanged(position) // Update UI for this item
                    release()
                    mediaPlayer = null
                }
            }
            notifyItemChanged(position) // Update UI for this item
        } else {
            if (isPlaying) {
                isPlaying = false
                statePlayer.playingAudio = false
                notifyItemChanged(position) // Update UI for this item
                // Pause playback if it's currently playing
                mediaPlayer?.pause()
            } else {
                // Resume playback if it's paused
                isPlaying = true
                statePlayer.playingAudio = true
                notifyItemChanged(position) // Update UI for this item
                mediaPlayer?.start()
            }
            notifyItemChanged(position) // Update UI for this item
        }
    }

    class MyViewHolder(val binding: AudioListCardBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(audio: AudioGamelanItem) {
            binding.audioName.text = audio.audioName
            binding.audioDescFull.text = audio.audioDesc
        }
    }
}

class AudioInstrumentAdapter(private val context: Context): RecyclerView.Adapter<AudioInstrumentAdapter.MyViewHolder>() {

    private lateinit var statePlayer: StatePlayer
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private var currentPlayingPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AudioListCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        statePlayer = StatePlayer(false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listAudio[position])
        val audio = listAudio[position]

        // Reflect playback state in the UI
        if (position == currentPlayingPosition && statePlayer.playingAudio) {
            holder.binding.playView.visibility = View.GONE
            holder.binding.pauseView.visibility = View.VISIBLE
            holder.binding.deskripsiTitle.visibility = View.GONE
            holder.binding.audioDescFull.visibility = View.GONE
        } else {
            holder.binding.playView.visibility = View.VISIBLE
            holder.binding.pauseView.visibility = View.GONE
            holder.binding.deskripsiTitle.visibility = View.GONE
            holder.binding.audioDescFull.visibility = View.GONE
        }

        holder.binding.button.setOnClickListener {
            playAudio(audio.audioPath, position)
        }

    }

    override fun getItemCount(): Int {
        return listAudio.size
    }

    private val listAudio = ArrayList<AudioInstrumenItem>()

    fun setListAudioInstrument(listAudioData: List<AudioInstrumenItem>) {
        val diffCallback = AudioInstrumentDiffCallback(this.listAudio, listAudioData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listAudio.clear()
        this.listAudio.addAll(listAudioData)
        diffResult.dispatchUpdatesTo(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun stopAudioPlayerInstrument() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
            currentPlayingPosition = RecyclerView.NO_POSITION
            notifyDataSetChanged() // Update UI to reflect stopped state
        }
    }

    private fun playAudio(audioUrl: String, position: Int) {
        if (mediaPlayer == null || currentPlayingPosition != position) {
            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null

                // Perbarui UI item sebelumnya
                val previousPosition = currentPlayingPosition
                currentPlayingPosition = position
                notifyItemChanged(previousPosition)
            }

            currentPlayingPosition = position

            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    this@AudioInstrumentAdapter.isPlaying = true
                    statePlayer.playingAudio = true
                    notifyItemChanged(position)
                    start()
                }
                setOnCompletionListener {
                    this@AudioInstrumentAdapter.isPlaying = false
                    statePlayer.playingAudio = false
                    notifyItemChanged(position)
                    release()
                    mediaPlayer = null
                }
            }
            notifyItemChanged(position)
        } else {
            if (isPlaying) {
                isPlaying = false
                statePlayer.playingAudio = false
                notifyItemChanged(position)
                mediaPlayer?.pause()
            } else {
                isPlaying = true
                statePlayer.playingAudio = true
                notifyItemChanged(position)
                mediaPlayer?.start()
            }
            notifyItemChanged(position)
        }
    }

    class MyViewHolder(val binding: AudioListCardBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(audio: AudioInstrumenItem) {
            binding.audioName.text = audio.audioName
        }
    }
}

class MaterialAdapter(private val context: Context): RecyclerView.Adapter<MaterialAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MaterialListCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listMaterial[position])
    }

    override fun getItemCount(): Int {
        return listMaterial.size
    }

    private val listMaterial = ArrayList<String>()

    fun setListMaterial(listMaterialData: List<String>) {
        val diffCallback = MaterialDiffCallback(this.listMaterial, listMaterialData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listMaterial.clear()
        this.listMaterial.addAll(listMaterialData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: MaterialListCardBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(material: String) {
            binding.materiallist.text = material

        }
    }
}

class InputMaterialAdapter(private val context: Context, private val onItemsChanged: (MutableList<String>) -> Unit): RecyclerView.Adapter<InputMaterialAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MaterialListCardInputBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listMaterial[position])

        holder.binding.deleteIconInput.setOnClickListener {
            listMaterial.removeAt(position)
            onItemsChanged(listMaterial)
        }
    }

    override fun getItemCount(): Int {
        return listMaterial.size
    }

    private val listMaterial = ArrayList<String>()

    fun setListMaterialInput(listMaterialData: List<String>) {
        val diffCallback = materialInputDiffCallback(this.listMaterial, listMaterialData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listMaterial.clear()
        this.listMaterial.addAll(listMaterialData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(val binding: MaterialListCardInputBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(material: String) {
            binding.materialName.text = material
        }
    }
}

class InputUpacaraGamelanAdapter(private val context: Context, private val onItemsChanged: (MutableList<String>) -> Unit): RecyclerView.Adapter<InputUpacaraGamelanAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MaterialListCardInputBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listUpacara[position])

        holder.binding.deleteIconInput.setOnClickListener {
            listUpacara.removeAt(position)
            onItemsChanged(listUpacara)
        }
    }

    override fun getItemCount(): Int {
        return listUpacara.size
    }

    private val listUpacara = ArrayList<String>()

    fun setListUpacaraInput(listUpacaraData: List<String>) {
        val diffCallback = materialInputDiffCallback(this.listUpacara, listUpacaraData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listUpacara.clear()
        this.listUpacara.addAll(listUpacaraData)
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(val binding: MaterialListCardInputBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(upacara: String) {
            binding.materialName.text = upacara
            binding.materialIcon.visibility = View.GONE
        }
    }
}

class InputAudioInstrumentAdapter(private val context: Context, private val onItemsChanged: (AudioArrayItem) -> Unit): RecyclerView.Adapter<InputAudioInstrumentAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AudioListInputBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listAudio[position])

        holder.binding.cardViewAudio.setOnClickListener {
            listAudio[position].flags = "edit"
            onItemsChanged(listAudio[position])
        }

        holder.binding.deleteIconInput.setOnClickListener {
            listAudio[position].flags = "delete"
            onItemsChanged(listAudio[position])
            this.listAudio.removeAt(position)
            Log.d("IsiDariListAudio", this.listAudio.toString())
        }
    }

    override fun getItemCount(): Int {
        return listAudio.size
    }

    private val listAudio = ArrayList<AudioArrayItem>()

    fun setListAudioInputInstrument(listAudioData: List<AudioArrayItem>) {
        val diffCallback = AudioInstrumentInputDiffCallback(this.listAudio, listAudioData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listAudio.clear()
        this.listAudio.addAll(listAudioData)
        Log.d("IsiDataketikadiisi", listAudioData.toString())
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(val binding: AudioListInputBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(audio: AudioArrayItem) {
            binding.audioName.text = audio.audioName
        }
    }
}

class InputAudioGamelanAdapter(private val context: Context, private val onItemsChanged: (AudioArrayGamelanItem) -> Unit): RecyclerView.Adapter<InputAudioGamelanAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AudioListInputBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listAudio[position])

        holder.binding.cardViewAudio.setOnClickListener {
            listAudio[position].flags = "edit"
            onItemsChanged(listAudio[position])
        }

        holder.binding.deleteIconInput.setOnClickListener {
            listAudio[position].flags = "delete"
            onItemsChanged(listAudio[position])
            this.listAudio.removeAt(position)
            Log.d("IsiDariListAudio", this.listAudio.toString())
        }
    }

    override fun getItemCount(): Int {
        return listAudio.size
    }

    private val listAudio = ArrayList<AudioArrayGamelanItem>()

    fun setListAudioGamelanInput(listAudioData: List<AudioArrayGamelanItem>) {
        val diffCallback = AudioGamelanInputDiffCallback(this.listAudio, listAudioData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listAudio.clear()
        this.listAudio.addAll(listAudioData)
        Log.d("IsiDataketikadiisi", listAudioData.toString())
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(val binding: AudioListInputBinding, private val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(audio: AudioArrayGamelanItem) {
            binding.audioName.text = audio.audioName
        }
    }
}

class AllUsersAdapter(private val context: Context, private val onItemClicked: (String) -> Unit): RecyclerView.Adapter<AllUsersAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UsersCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listUsers[position])

        holder.itemView.setOnClickListener {
            onItemClicked(listUsers[position].id)
        }
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    private val listUsers = ArrayList<DataUserItem>()

    fun setListUsers(listUsersData: List<DataUserItem>) {
        val diffCallback = AllUsersDataDiffCallback(this.listUsers, listUsersData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listUsers.clear()
        this.listUsers.addAll(listUsersData)
        diffResult.dispatchUpdatesTo(this)
        Log.d("IsiSDariListUsers", listUsers.toString())
    }

    class MyViewHolder(val binding: UsersCardBinding, val context: Context): RecyclerView.ViewHolder (binding.root) {
        fun bind(user: DataUserItem) {
            binding.fullNameUsers.text = user.nama
            binding.emailUsers.text = user.email

            if (user.fotoProfile != "none") {
                Glide.with(binding.root)
                    .load(user.fotoProfile)
                    .transform(CenterCrop(), RoundedCorners(100))
                    .into(binding.profileImageAdmin)
            }
        }
    }
}
